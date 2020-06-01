create table opetussuunnitelma_aikataulu (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tapahtuma varchar(255),
    tapahtumapaiva timestamp,
    opetussuunnitelma_id int8,
    tavoite_id int8,
    primary key (id)
);

create table opetussuunnitelma_aikataulu_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tapahtuma varchar(255),
    tapahtumapaiva timestamp,
    opetussuunnitelma_id int8,
    tavoite_id int8,
    primary key (id, REV)
);

alter table opetussuunnitelma_aikataulu
    add constraint FKp1mw3samoy5gir9a6kouid5su
    foreign key (opetussuunnitelma_id)
    references opetussuunnitelma;

alter table opetussuunnitelma_aikataulu
    add constraint FKouym1slb9fwaj20slhehrj2hh
    foreign key (tavoite_id)
    references lokalisoituteksti;

alter table opetussuunnitelma_aikataulu_AUD
    add constraint FKn9jlww4c4dj8vh85padpko8vw
    foreign key (REV)
    references revinfo;

alter table opetussuunnitelma_aikataulu_AUD
    add constraint FK2u9bdumxr3fj33asmayyxcsxx
    foreign key (REVEND)
    references revinfo;
