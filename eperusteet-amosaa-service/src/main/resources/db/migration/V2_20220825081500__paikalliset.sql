-- Sisaltoviitelinkki
ALTER TABLE sisaltoviite ADD COLUMN linkkiSisaltoViite_id int8 DEFAULT NULL;
ALTER TABLE sisaltoviite_aud ADD COLUMN linkkiSisaltoViite_id int8 DEFAULT NULL;

alter table sisaltoviite
    add constraint FK_dc22nlclxkoonj4k76v3a1djr
    foreign key (linkkiSisaltoViite_id)
    references sisaltoviite;

-- Paikallisten ja perusteeseen liittyvien osa-alueiden sisällöt
create table omaosaalue (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    tyyppi varchar(255) not null,
    perusteenosaaluekoodi varchar(255),
    perusteenosaalueid int8,
    piilotettu boolean not null,
    arvioinnista_id int8,
    tavatjaymparisto_id int8,
    primary key (id)
);

create table omaosaalue_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    piilotettu boolean not null,
    tyyppi varchar(255) not null,
    perusteenosaaluekoodi varchar(255),
    perusteenosaalueid int8,
    arvioinnista_id int8,
    tavatjaymparisto_id int8,
    primary key (id, REV)
);

create table sisaltoviite_omaosaalue (
    sisaltoviite_id int8 not null,
    osaAlueet_id int8 not null
);

create table sisaltoviite_omaosaalue_AUD (
    REV int4 not null,
    sisaltoviite_id int8 not null,
    osaAlueet_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, sisaltoviite_id, osaAlueet_id)
);

create table omaosaalue_vapaa_teksti (
    omaosaalue_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    primary key (omaosaalue_id, jnro)
);

alter table sisaltoviite_omaosaalue
    add constraint UK_ihwf5f3ske0jbd6egp00lcryy unique (osaAlueet_id);

create table omaosaalue_vapaa_teksti_AUD (
    REV int4 not null,
    omaosaalue_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, omaosaalue_id, vapaat_id, jnro)
);

create table osaalueenosaamistavoitteet_omaosaalue (
    omaosaalue_id int8 not null,
    osaalueenosaamistavoitteet_id int8 not null,
    jarjestys int4 not null,
    primary key (omaosaalue_id, jarjestys)
);

create table osaalueenosaamistavoitteet_omaosaalue_AUD (
    REV int4 not null,
    omaosaalue_id int8 not null,
    osaalueenosaamistavoitteet_id int8 not null,
    jarjestys int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, omaosaalue_id, osaalueenosaamistavoitteet_id, jarjestys)
);


create table osaalueenosaamistavoitteet (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    otsikko_id int8,
    primary key (id)
);

create table osaalueenosaamistavoitteet_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    otsikko_id int8,
    primary key (id, REV)
);

create table osaalueenosaamistavoitteet_yto_osaamistavoite (
    osaalueenosaamistavoitteet_id int8 not null,
    osaamistavoitteet_id int8 not null
);

create table osaalueenosaamistavoitteet_yto_osaamistavoite_AUD (
    REV int4 not null,
    osaalueenosaamistavoitteet_id int8 not null,
    osaamistavoitteet_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, osaalueenosaamistavoitteet_id, osaamistavoitteet_id)
);

create table yto_osaamistavoite (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    selite_id int8,
    primary key (id)
);

create table yto_osaamistavoite_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    selite_id int8,
    primary key (id, REV)
);

alter table osaalueenosaamistavoitteet_yto_osaamistavoite
    add constraint UK_dgxbw8fvi2lyot7p0nevnq58n  unique (osaamistavoitteet_id);

alter table omaosaalue_vapaa_teksti
    add constraint UK_1t2yl85adavneqlm2odnv5q9c  unique (vapaat_id);

alter table osaalueenosaamistavoitteet_omaosaalue
    add constraint UK_86phptc1f3qoaigs5uvo4vyt6  unique (osaalueenosaamistavoitteet_id);

alter table osaalueenosaamistavoitteet
    add constraint FK_jlwflwc6vjdygxoa5u9h8wqp8
    foreign key (otsikko_id)
    references lokalisoituteksti;

alter table osaalueenosaamistavoitteet_AUD
    add constraint FK_4t1jg378rv35ylxpt66weq7ga
    foreign key (REV)
    references revinfo;

alter table osaalueenosaamistavoitteet_AUD
    add constraint FK_7303fnrvq29k8kdnuc08e9cps
    foreign key (REVEND)
    references revinfo;

alter table osaalueenosaamistavoitteet_omaosaalue
    add constraint FK_86phptc1f3qoaigs5uvo4vyt6
    foreign key (osaalueenosaamistavoitteet_id)
    references osaalueenosaamistavoitteet;

alter table osaalueenosaamistavoitteet_omaosaalue
    add constraint FK_ajaed1lfko1u5vrb2kr0w7y2e
    foreign key (omaosaalue_id)
    references omaosaalue;

alter table osaalueenosaamistavoitteet_omaosaalue_AUD
    add constraint FK_ffaxbk97l6821g56oon7qljuj
    foreign key (REV)
    references revinfo;

alter table osaalueenosaamistavoitteet_omaosaalue_AUD
    add constraint FK_8gg3t3vidncgnio9uhd5ug6e2
    foreign key (REVEND)
    references revinfo;

alter table osaalueenosaamistavoitteet_yto_osaamistavoite
    add constraint FK_dgxbw8fvi2lyot7p0nevnq58n
    foreign key (osaamistavoitteet_id)
    references yto_osaamistavoite;

alter table osaalueenosaamistavoitteet_yto_osaamistavoite
    add constraint FK_qtl4epxu7it09bkd1mwpp1j1j
    foreign key (osaalueenosaamistavoitteet_id)
    references osaalueenosaamistavoitteet;

alter table osaalueenosaamistavoitteet_yto_osaamistavoite_AUD
    add constraint FK_2bp5474u9dyx3bbm21ebubujs
    foreign key (REV)
    references revinfo;

alter table osaalueenosaamistavoitteet_yto_osaamistavoite_AUD
    add constraint FK_ma38d9k3flwsvr69cfksf832l
    foreign key (REVEND)
    references revinfo;

alter table yto_osaamistavoite
    add constraint FK_199jos2b6fv3dq4b7yoxbaikx
    foreign key (selite_id)
    references lokalisoituteksti;

alter table yto_osaamistavoite_AUD
    add constraint FK_4cewm6au3xthioygyc3ggm3l8
    foreign key (REV)
    references revinfo;

alter table yto_osaamistavoite_AUD
    add constraint FK_pex0gljv57bdmery3g7l4wvoi
    foreign key (REVEND)
    references revinfo;

alter table omaosaalue
    add constraint FK_52a1u39n74b6kja9jcesr5ja3
    foreign key (arvioinnista_id)
    references tekstiosa;

alter table omaosaalue
    add constraint FK_6fg0v6lj0dvlp0fgruo7f8r5s
    foreign key (tavatjaymparisto_id)
    references tekstiosa;

alter table omaosaalue_AUD
    add constraint FK_9r7teosxxiotk5q9cdy1g2uy7
    foreign key (REV)
    references revinfo;

alter table omaosaalue_AUD
    add constraint FK_favuv1x6todvfcrioxc5kfbrs
    foreign key (REVEND)
    references revinfo;

alter table omaosaalue_vapaa_teksti
    add constraint FK_1t2yl85adavneqlm2odnv5q9c
    foreign key (vapaat_id)
    references vapaa_teksti;

alter table omaosaalue_vapaa_teksti
    add constraint FK_gf0flf17lbuv2a7w44b73oi9x
    foreign key (omaosaalue_id)
    references omaosaalue;

alter table omaosaalue_vapaa_teksti_AUD
    add constraint FK_m0yyrp3ksxo76906mq9db3cbo
    foreign key (REV)
    references revinfo;

alter table omaosaalue_vapaa_teksti_AUD
    add constraint FK_r0l16n2twsktv9il97etr2pui
    foreign key (REVEND)
    references revinfo;