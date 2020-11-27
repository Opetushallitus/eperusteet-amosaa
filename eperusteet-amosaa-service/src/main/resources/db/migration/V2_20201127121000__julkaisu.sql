create table julkaisu (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    revision int4 not null,
    data_id int8,
    opetussuunnitelma_id int8,
    tiedote_id int8,
    primary key (id)
);

create table julkaisu_data (
    id int8 not null,
    data jsonb not null,
    hash int4 not null,
    primary key (id)
);

create table Julkaisu_dokumentit (
    Julkaisu_id int8 not null,
    dokumentit int8
);

alter table julkaisu
    add constraint FK_nbxfacvx9kqqutsvroex5d2tf
    foreign key (data_id)
    references julkaisu_data;

alter table julkaisu
    add constraint FK_7w3d7bmb0f3fro8sbgp736vj1
    foreign key (opetussuunnitelma_id)
    references opetussuunnitelma;

alter table julkaisu
    add constraint FK_o6n6k61i2xbuhqi1gct20582c
    foreign key (tiedote_id)
    references lokalisoituteksti;

alter table Julkaisu_dokumentit
    add constraint FKmp80manm2q91osr03kpnxrmx2
    foreign key (Julkaisu_id)
    references julkaisu;
