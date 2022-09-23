ALTER TABLE opetussuunnitelma ADD COLUMN IF NOT EXISTS jotpatyyppi varchar(255);
ALTER TABLE opetussuunnitelma_aud ADD COLUMN IF NOT EXISTS jotpatyyppi varchar(255);

drop trigger if exists tg_refresh_opetussuunnitelma_data_view_after_tila_update on opetussuunnitelma;
drop materialized view if exists julkaistu_opetussuunnitelma_data_view;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on julkaisu;
drop trigger if exists tg_refresh_julkaistu_opetussuunnitelma_data_view on opetussuunnitelma;
drop function if exists tg_refresh_julkaistu_opetussuunnitelma_data_view;

create materialized view julkaistu_opetussuunnitelma_data_view as
SELECT
    d.data->'id' as id,
    d.data->'nimi' as nimi,
    d.data->'koulutustoimija' as koulutustoimija,
    d.data->'peruste' as peruste,
    d.data->>'oppilaitosTyyppiKoodiUri' as "oppilaitosTyyppiKoodiUri",
    d.data->>'tyyppi' as tyyppi,
    d.data->'opintokokonaisuudet' as opintokokonaisuudet,
    COALESCE(d.data->>'koulutustyyppi', o.koulutustyyppi) as koulutustyyppi,
    data->>'voimaantulo' as "voimaantulo",
    data->>'voimassaoloLoppuu' as "voimassaoloLoppuu",
    d.data->>'jotpatyyppi' as jotpatyyppi
FROM julkaisu j
    INNER JOIN julkaisu_data d on d.id = j.data_id
    INNER JOIN opetussuunnitelma o on o.id = j.opetussuunnitelma_id
WHERE revision = (SELECT MAX(revision) FROM julkaisu j2 WHERE j.opetussuunnitelma_id = j2.opetussuunnitelma_id)
  AND o.tila != 'POISTETTU';

CREATE UNIQUE INDEX ON julkaistu_opetussuunnitelma_data_view (id);

CREATE OR REPLACE FUNCTION tg_refresh_julkaistu_opetussuunnitelma_data_view()
RETURNS trigger AS
'
BEGIN
	REFRESH MATERIALIZED VIEW CONCURRENTLY julkaistu_opetussuunnitelma_data_view;
	RETURN null;
END
'
LANGUAGE plpgsql;

CREATE TRIGGER tg_refresh_julkaistu_opetussuunnitelma_data_view AFTER INSERT
    ON julkaisu
    FOR EACH STATEMENT EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();

CREATE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update
    AFTER UPDATE OF tila ON opetussuunnitelma
    FOR EACH ROW
    EXECUTE PROCEDURE tg_refresh_julkaistu_opetussuunnitelma_data_view();


