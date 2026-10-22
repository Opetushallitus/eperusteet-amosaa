alter table sisaltoviite add column tavoitesisaltoalue_id int8;
alter table sisaltoviite_aud add column tavoitesisaltoalue_id int8;

create table tavoitesisaltoalue (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    paikallinenTarkennus_id int8,
    primary key (id)
);

create table tavoitesisaltoalue_AUD (
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

alter table tavoitesisaltoalue
    add constraint FK_tavoitesisaltoalue_paikallinen_tarkennus
    foreign key (paikallinenTarkennus_id)
    references lokalisoituteksti;

alter table tavoitesisaltoalue_AUD
    add constraint FK_tavoitesisaltoalue_aud_rev
    foreign key (REV)
    references revinfo;

alter table tavoitesisaltoalue_AUD
    add constraint FK_tavoitesisaltoalue_aud_revend
    foreign key (REVEND)
    references revinfo;
