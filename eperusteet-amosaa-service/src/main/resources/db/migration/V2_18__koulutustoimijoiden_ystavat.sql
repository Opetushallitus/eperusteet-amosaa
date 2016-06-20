create table Koulutustoimija_ystavat (
    Koulutustoimija_id int8 not null,
    ystavat varchar(255)
);

create table Koulutustoimija_ystavat_AUD (
    REV int4 not null,
    Koulutustoimija_id int8 not null,
    ystavat varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Koulutustoimija_id, ystavat)
);

alter table Koulutustoimija_ystavat
    add constraint FK_7xf3rhbsu006g5ijcuyeecgl0
    foreign key (Koulutustoimija_id)
    references koulutustoimija;

alter table Koulutustoimija_ystavat_AUD
    add constraint FK_o1pqnd89ucuywa68m5dt67dvg
    foreign key (REV)
    references revinfo;

alter table Koulutustoimija_ystavat_AUD
    add constraint FK_6ec1uo45oh280yuygbybrfp02
    foreign key (REVEND)
    references revinfo;
