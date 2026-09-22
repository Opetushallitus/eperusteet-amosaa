-- Replace materialized view with a regular table maintained incrementally.
-- REFRESH MATERIALIZED VIEW CONCURRENTLY rebuilds the whole view on every julkaisu
-- insert (JSONB extract from every opsdata row) and runs inside the same transaction,
-- which makes publication very slow.

drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on julkaisu;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelma;
drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;

create table julkaistu_opetussuunnitelma_data_view_new as
select * from julkaistu_opetussuunnitelma_data_view;

alter table julkaistu_opetussuunnitelma_data_view_new
    add constraint julkaistu_opetussuunnitelma_data_view_pkey primary key (id);

drop materialized view if exists julkaistu_opetussuunnitelma_data_view;
drop function if exists tg_refresh_julkaistu_opetussuunnitelma_data_view();

alter table julkaistu_opetussuunnitelma_data_view_new rename to julkaistu_opetussuunnitelma_data_view;

create or replace function upsert_julkaistu_opetussuunnitelma_data_view(p_data_id bigint, p_ops_id bigint)
returns void as $$
begin
    insert into julkaistu_opetussuunnitelma_data_view (
        id,
        nimi,
        koulutustoimija,
        peruste,
        "oppilaitosTyyppiKoodiUri",
        tyyppi,
        opintokokonaisuudet,
        paikallistasisaltoa,
        koulutustyyppi,
        "voimaantulo",
        "voimassaoloLoppuu",
        jotpatyyppi,
        julkaisukielet
    )
    select
        d.data->'id',
        d.data->'nimi',
        d.data->'koulutustoimija',
        d.data->'peruste',
        d.data->>'oppilaitosTyyppiKoodiUri',
        d.data->>'tyyppi',
        d.data->'opintokokonaisuudet',
        exists (
            select 1
            from jsonb_array_elements(coalesce(d.data->'tutkinnonOsat', '[]'::jsonb)) elem
            where elem->'tosa'->>'tyyppi' = 'oma'
        ),
        coalesce(d.data->>'koulutustyyppi', o.koulutustyyppi),
        d.data->>'voimaantulo',
        d.data->>'voimassaoloLoppuu',
        d.data->>'jotpatyyppi',
        d.data->'julkaisukielet'
    from julkaisu_data d
    inner join opetussuunnitelma o on o.id = p_ops_id
    where d.id = p_data_id
      and o.tila != 'POISTETTU'
    on conflict (id) do update set
        nimi = excluded.nimi,
        koulutustoimija = excluded.koulutustoimija,
        peruste = excluded.peruste,
        "oppilaitosTyyppiKoodiUri" = excluded."oppilaitosTyyppiKoodiUri",
        tyyppi = excluded.tyyppi,
        opintokokonaisuudet = excluded.opintokokonaisuudet,
        paikallistasisaltoa = excluded.paikallistasisaltoa,
        koulutustyyppi = excluded.koulutustyyppi,
        "voimaantulo" = excluded."voimaantulo",
        "voimassaoloLoppuu" = excluded."voimassaoloLoppuu",
        jotpatyyppi = excluded.jotpatyyppi,
        julkaisukielet = excluded.julkaisukielet;
end;
$$ language plpgsql;

create or replace function tg_upsert_julkaistu_opetussuunnitelma_data_view()
returns trigger as $$
begin
    perform upsert_julkaistu_opetussuunnitelma_data_view(new.data_id, new.opetussuunnitelma_id);
    return null;
end;
$$ language plpgsql;

create trigger tg_refresh_julkaistu_opetussuunnitelma_data_view
    after insert on julkaisu
    for each row
    execute procedure tg_upsert_julkaistu_opetussuunnitelma_data_view();

create or replace function tg_sync_julkaistu_opetussuunnitelma_data_view_tila()
returns trigger as $$
begin
    if new.tila = 'POISTETTU' then
        delete from julkaistu_opetussuunnitelma_data_view
        where (id #>> '{}') = new.id::text;
    else
        perform upsert_julkaistu_opetussuunnitelma_data_view(j.data_id, new.id)
        from julkaisu j
        where j.opetussuunnitelma_id = new.id
        order by j.revision desc
        limit 1;
    end if;
    return null;
end;
$$ language plpgsql;

create trigger tg_refresh_opetussuunnitelma_data_view_after_tila_update
    after update on opetussuunnitelma
    for each row
    when (new.tila <> old.tila
        and (old.tila = 'POISTETTU' or new.tila = 'POISTETTU'))
    execute procedure tg_sync_julkaistu_opetussuunnitelma_data_view_tila();
