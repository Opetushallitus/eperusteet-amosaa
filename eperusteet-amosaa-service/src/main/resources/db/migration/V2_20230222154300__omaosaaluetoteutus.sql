drop table omaosaalue_vapaa_teksti;
drop table omaosaalue_vapaa_teksti_AUD;

alter table omaosaalue drop column arvioinnista_id;
alter table omaosaalue drop column tavatjaymparisto_id;

alter table omaosaalue_aud drop column arvioinnista_id;
alter table omaosaalue_aud drop column tavatjaymparisto_id;

alter table omaosaalue add column laajuus int4;
alter table omaosaalue_aud add column laajuus int4;

create table omaosaalue_omaosaaluetoteutus (
    omaosaalue_id int8 not null,
    toteutukset_id int8 not null,
    jnro int4 not null,
    primary key (omaosaalue_id, jnro)
);

create table omaosaalue_omaosaaluetoteutus_AUD (
    REV int4 not null,
    omaosaalue_id int8 not null,
    toteutukset_id int8 not null,
    jnro int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, omaosaalue_id, toteutukset_id, jnro)
);

create table omaosaaluetoteutus (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    arvioinnista_id int8,
    otsikko_id int8,
    tavatjaymparisto_id int8,
    primary key (id)
);

create table omaosaaluetoteutus_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    arvioinnista_id int8,
    otsikko_id int8,
    tavatjaymparisto_id int8,
    primary key (id, REV)
);

create table omaosaaluetoteutus_vapaa_teksti (
    omaosaaluetoteutus_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    primary key (omaosaaluetoteutus_id, jnro)
);

create table omaosaaluetoteutus_vapaa_teksti_AUD (
    REV int4 not null,
    omaosaaluetoteutus_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, omaosaaluetoteutus_id, vapaat_id, jnro)
);

alter table omaosaalue_omaosaaluetoteutus
    add constraint UK_q2qjd31p281uvb6api7wb010h  unique (toteutukset_id);

alter table omaosaaluetoteutus_vapaa_teksti
    add constraint UK_f0auloie9q4hmadnk8tkeds0n  unique (vapaat_id);

alter table omaosaalue_omaosaaluetoteutus
    add constraint FK_q2qjd31p281uvb6api7wb010h
    foreign key (toteutukset_id)
    references omaosaaluetoteutus;

alter table omaosaalue_omaosaaluetoteutus
    add constraint FK_q02iff0bxg41e2yu5lshm20j2
    foreign key (omaosaalue_id)
    references omaosaalue;

alter table omaosaalue_omaosaaluetoteutus_AUD
    add constraint FK_nvhg421dbct0sq5t5ocmdje0a
    foreign key (REV)
    references revinfo;

alter table omaosaalue_omaosaaluetoteutus_AUD
    add constraint FK_rlf7vcta0haahepiip31ubh27
    foreign key (REVEND)
    references revinfo;

alter table omaosaaluetoteutus
    add constraint FK_k7ptod6eh40of5kf5fcft5nn7
    foreign key (arvioinnista_id)
    references lokalisoituteksti;

alter table omaosaaluetoteutus
    add constraint FK_regcohhojknm0dheby5120c5q
    foreign key (otsikko_id)
    references lokalisoituteksti;

alter table omaosaaluetoteutus
    add constraint FK_jl7o5akd4adh3fynwjui9ryqi
    foreign key (tavatjaymparisto_id)
    references lokalisoituteksti;

alter table omaosaaluetoteutus_AUD
    add constraint FK_1t44sn3eopxskikuadp7m2ohx
    foreign key (REV)
    references revinfo;

alter table omaosaaluetoteutus_AUD
    add constraint FK_qrex76p8kyvk24twcxac7w5em
    foreign key (REVEND)
    references revinfo;

alter table omaosaaluetoteutus_vapaa_teksti
    add constraint FK_f0auloie9q4hmadnk8tkeds0n
    foreign key (vapaat_id)
    references vapaa_teksti;

alter table omaosaaluetoteutus_vapaa_teksti
    add constraint FK_p5r1lbgc0n5fqthutv7kqf0bc
    foreign key (omaosaaluetoteutus_id)
    references omaosaaluetoteutus;

alter table omaosaaluetoteutus_vapaa_teksti_AUD
    add constraint FK_1vfgsf4ltmsgh524qjau637fx
    foreign key (REV)
    references revinfo;

alter table omaosaaluetoteutus_vapaa_teksti_AUD
    add constraint FK_8b4bhvx2ljk5l3mkb0hlxnqly
    foreign key (REVEND)
    references revinfo;