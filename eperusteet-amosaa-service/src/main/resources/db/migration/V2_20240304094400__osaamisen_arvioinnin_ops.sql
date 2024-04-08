create table osaamisen_arvioinnin_toteutussuunnitelma (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    nimi_id int8,
    oat_id int8,
    opetussuunnitelma_id int8 not null,
    primary key (id)
);

create table osaamisen_arvioinnin_toteutussuunnitelma_url (
    OsaamisenArvioinninToteutussuunnitelma_id int8 not null,
    url varchar(255),
    url_KEY int4,
    primary key (OsaamisenArvioinninToteutussuunnitelma_id, url_KEY)
);

alter table osaamisen_arvioinnin_toteutussuunnitelma
    add constraint FK_hjxlorg0ryeq7xedhr2tm5lgo
    foreign key (nimi_id)
    references lokalisoituteksti;

alter table osaamisen_arvioinnin_toteutussuunnitelma
    add constraint FK_l3npo81v6afutyvtxx1jx0h6k
    foreign key (oat_id)
    references opetussuunnitelma;

alter table osaamisen_arvioinnin_toteutussuunnitelma
    add constraint FK_ar01bhwc4vb4llviixaydirbd
    foreign key (opetussuunnitelma_id)
    references opetussuunnitelma;

alter table osaamisen_arvioinnin_toteutussuunnitelma_url
    add constraint FK_99uyrh4cndkub8qsf9trsmmec
    foreign key (OsaamisenArvioinninToteutussuunnitelma_id)
    references osaamisen_arvioinnin_toteutussuunnitelma;