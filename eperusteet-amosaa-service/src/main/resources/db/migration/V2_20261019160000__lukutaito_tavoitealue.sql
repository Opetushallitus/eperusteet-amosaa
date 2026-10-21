alter table sisaltoviite add column lukutaitoTavoitealue_id int8;
alter table sisaltoviite_aud add column lukutaitoTavoitealue_id int8;

create table lukutaito_tavoitealue (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    paikallinenTarkennus_id int8,
    primary key (id)
);

create table lukutaito_tavoitealue_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    paikallinenTarkennus_id int8,
    primary key (id, REV)
);

alter table sisaltoviite
    add constraint FK_sisaltoviite_lukutaito_tavoitealue
    foreign key (lukutaitoTavoitealue_id)
    references lukutaito_tavoitealue;

alter table lukutaito_tavoitealue
    add constraint FK_lukutaito_tavoitealue_teksti
    foreign key (paikallinenTarkennus_id)
    references lokalisoituteksti;

alter table lukutaito_tavoitealue_AUD
    add constraint FK_lukutaito_tavoitealue_aud_rev
    foreign key (REV)
    references revinfo;

alter table lukutaito_tavoitealue_AUD
    add constraint FK_lukutaito_tavoitealue_aud_revend
    foreign key (REVEND)
    references revinfo;
