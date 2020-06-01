create table opetussuunnitelma_muokkaustieto (
    id int8 not null,
    kohde varchar(255),
    kohde_id int8,
    lisatieto varchar(255),
    luotu timestamp,
    muokkaaja varchar(255),
    opetussuunnitelma_id int8,
    poistettu boolean not null,
    tapahtuma varchar(255),
    nimi_id int8,
    primary key (id)
);

alter table opetussuunnitelma_muokkaustieto
    add constraint FKokuo35bysusfus2sg4rrwebxm
    foreign key (nimi_id)
    references lokalisoituteksti;
