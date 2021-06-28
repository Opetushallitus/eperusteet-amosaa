alter table sisaltoviite drop column IF EXISTS koulutuksenosa_id;
alter table sisaltoviite_aud drop column IF EXISTS koulutuksenosa_id;
alter table sisaltoviite drop column IF EXISTS tuvaLaajaAlainenOsaaminen_id;
alter table sisaltoviite_aud drop column IF EXISTS tuvaLaajaAlainenOsaaminen_id;

drop table if exists paikallinen_koulutuksenosa_tavoitteet;
drop table if exists paikallinen_koulutuksenosa_tavoitteet_AUD;
drop table if exists koulutuksenosan_paikallinen_tarkennus CASCADE;
drop table if exists koulutuksenosan_paikallinen_tarkennus_AUD;
drop table if exists koulutuksenosan_laajaalainen_osaaminen;
drop table if exists koulutuksenosan_laajaalainen_osaaminen_AUD;
drop table if exists koulutuksenjarjestaja;
drop table if exists koulutuksenjarjestaja_AUD;
drop table if exists tuva_laajaalainenosaaminen;
drop table if exists tuva_laajaalainenosaaminen_AUD;
drop table if exists koulutuksenosa_tavoitteet;
drop table if exists koulutuksenosa_tavoitteet_AUD;
drop table if exists koulutuksenosa;
drop table if exists koulutuksenosa_AUD;

alter table sisaltoviite add column koulutuksenosa_id int8;
alter table sisaltoviite_aud add column koulutuksenosa_id int8;

alter table sisaltoviite add column tuvaLaajaAlainenOsaaminen_id int8;
alter table sisaltoviite_aud add column tuvaLaajaAlainenOsaaminen_id int8;

create table koulutuksenosa (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koulutusOsanKoulutustyyppi varchar(255),
    koulutusOsanTyyppi varchar(255),
    laajuusMaksimi int4,
    laajuusMinimi int4,
    nimiKoodi varchar(255),
    arvioinninKuvaus_id int8,
    keskeinenSisalto_id int8,
    kuvaus_id int8,
    laajaAlaisenOsaamisenKuvaus_id int8,
    nimi_id int8,
    paikallinenTarkennus_id int8,
    primary key (id)
);

create table koulutuksenosa_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koulutusOsanKoulutustyyppi varchar(255),
    koulutusOsanTyyppi varchar(255),
    laajuusMaksimi int4,
    laajuusMinimi int4,
    nimiKoodi varchar(255),
    arvioinninKuvaus_id int8,
    keskeinenSisalto_id int8,
    kuvaus_id int8,
    laajaAlaisenOsaamisenKuvaus_id int8,
    nimi_id int8,
    paikallinenTarkennus_id int8,
    primary key (id, REV)
);

create table koulutuksenosa_tavoitteet (
    koulutuksenosa_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    primary key (koulutuksenosa_id, tavoitteet_ORDER)
);

create table koulutuksenosa_tavoitteet_AUD (
    REV int4 not null,
    koulutuksenosa_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, koulutuksenosa_id, tavoite_id, tavoitteet_ORDER)
);

create table tuva_laajaalainenosaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    liite boolean not null,
    nimiKoodi varchar(255),
    teksti_id int8,
    primary key (id)
);

create table tuva_laajaalainenosaaminen_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    liite boolean,
    nimiKoodi varchar(255),
    teksti_id int8,
    primary key (id, REV)
);

create table koulutuksenjarjestaja (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    nimi_id int8,
    oid varchar(255),
    paikallinenTarkennus_id int8,
    url_id int8,
    koulutuksenJarjestajat_ORDER int4,
    primary key (id)
);

create table koulutuksenjarjestaja_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    kuvaus_id int8,
    nimi_id int8,
    oid varchar(255),
    paikallinenTarkennus_id int8,
    url_id int8,
    primary key (id, REV)
);

create table koulutuksenosan_laajaalainen_osaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    laajaAlaisenOsaamisenKuvaus_id int8,
    paikallinenTarkennus_id int8,
    laajaalaisetosaamiset_ORDER int4,
    primary key (id)
);

create table koulutuksenosan_laajaalainen_osaaminen_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    laajaAlaisenOsaamisenKuvaus_id int8,
    paikallinenTarkennus_id int8,
    primary key (id, REV)
);

create table koulutuksenosan_paikallinen_tarkennus (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    arvioinninKuvaus_id int8,
    keskeinenSisalto_id int8,
    tavoitteetKuvaus_id int8,
    primary key (id)
);

create table koulutuksenosan_paikallinen_tarkennus_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    arvioinninKuvaus_id int8,
    keskeinenSisalto_id int8,
    tavoitteetKuvaus_id int8,
    primary key (id, REV)
);

create table paikallinen_koulutuksenosa_tavoitteet (
    paikallinen_koulutuksenosa_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    primary key (paikallinen_koulutuksenosa_id, tavoitteet_ORDER)
);

create table paikallinen_koulutuksenosa_tavoitteet_AUD (
    REV int4 not null,
    paikallinen_koulutuksenosa_id int8 not null,
    tavoite_id int8 not null,
    tavoitteet_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, paikallinen_koulutuksenosa_id, tavoite_id, tavoitteet_ORDER)
);

alter table koulutuksenosa
    add constraint FK_tijy5iff70pywvluo4kenvndk
    foreign key (arvioinninKuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosa
    add constraint FK_8gr6h21gjsfe7gf91itwyeta0
    foreign key (keskeinenSisalto_id)
    references lokalisoituteksti;

alter table koulutuksenosa
    add constraint FK_g8xkabegrpcoyaye2srp4dwfk
    foreign key (kuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosa
    add constraint FK_qql3kp58p6xq5g87h2sdawba9
    foreign key (laajaAlaisenOsaamisenKuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosa
    add constraint FK_dyn0jj7742lp6w8p44fs259qk
    foreign key (nimi_id)
    references lokalisoituteksti;

alter table koulutuksenosa
    add constraint FK_6x6c2qoihppvek0s0wssi853a
    foreign key (paikallinenTarkennus_id)
    references koulutuksenosan_paikallinen_tarkennus;

alter table koulutuksenosa_AUD
    add constraint FK_awptejlsww6e3igm6m6jxiak
    foreign key (REV)
    references revinfo;

alter table koulutuksenosa_AUD
    add constraint FK_tql0tw3v3rt8ocs2r4rwqmhyo
    foreign key (REVEND)
    references revinfo;

alter table koulutuksenosa_tavoitteet
    add constraint FK_9a1vyacy4o2xn0wqk14ju5ayj
    foreign key (tavoite_id)
    references lokalisoituteksti;

alter table koulutuksenosa_tavoitteet
    add constraint FK_a5t1wwqoo78t27xe58yo8usor
    foreign key (koulutuksenosa_id)
    references koulutuksenosa;

alter table koulutuksenosa_tavoitteet_AUD
    add constraint FK_67u3s7mm7hdhwwcd5yyfhbl9a
    foreign key (REV)
    references revinfo;

alter table koulutuksenosa_tavoitteet_AUD
    add constraint FK_23xjff3ebq11n2pa6qill8d45
    foreign key (REVEND)
    references revinfo;

alter table koulutuksenosan_laajaalainen_osaaminen
    add constraint FK_inokm9j5vqngm8oadqls6w58e
    foreign key (laajaAlaisenOsaamisenKuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosan_laajaalainen_osaaminen
    add constraint FK_q4u2cro1djon0kdpc4f12l5b3
    foreign key (paikallinenTarkennus_id)
    references koulutuksenosan_paikallinen_tarkennus;

alter table koulutuksenosan_laajaalainen_osaaminen_AUD
    add constraint FK_bx807ws8tj78shes1d5r9jss1
    foreign key (REV)
    references revinfo;

alter table koulutuksenosan_laajaalainen_osaaminen_AUD
    add constraint FK_rbp5rcabtbd2l6cg19t54c5cn
    foreign key (REVEND)
    references revinfo;

alter table koulutuksenosan_paikallinen_tarkennus
    add constraint FK_ixuga7a16n9bf8er5kgthhb8u
    foreign key (arvioinninKuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosan_paikallinen_tarkennus
    add constraint FK_seg10ycixtmpw6xk9bqi5rfg2
    foreign key (keskeinenSisalto_id)
    references lokalisoituteksti;

alter table koulutuksenosan_paikallinen_tarkennus
    add constraint FK_nihbfk0vamw6odljbowcvnq2u
    foreign key (tavoitteetKuvaus_id)
    references lokalisoituteksti;

alter table koulutuksenosan_paikallinen_tarkennus_AUD
    add constraint FK_nh9fwebtt23f1lqbtv9w7v6qg
    foreign key (REV)
    references revinfo;

alter table koulutuksenosan_paikallinen_tarkennus_AUD
    add constraint FK_59upo3f0y6oa8adjmh5gqps4n
    foreign key (REVEND)
    references revinfo;

alter table sisaltoviite
    add constraint FK_86n3o7bxbu9b4ole0ucoojbln
    foreign key (koulutuksenosa_id)
    references koulutuksenosa;

alter table sisaltoviite
    add constraint FK_2ma4p6diw9dd30rl5ilahnobw
    foreign key (tuvaLaajaAlainenOsaaminen_id)
    references tuva_laajaalainenosaaminen;

alter table tuva_laajaalainenosaaminen
    add constraint FK_bjtpdcfgmdgy5ylrle7hc3puv
    foreign key (teksti_id)
    references lokalisoituteksti;

alter table tuva_laajaalainenosaaminen_AUD
    add constraint FK_5rbuo932ra089axmphlkh1f0b
    foreign key (REV)
    references revinfo;

alter table tuva_laajaalainenosaaminen_AUD
    add constraint FK_a65b6xk9ayh2wh6x3l0apumkt
    foreign key (REVEND)
    references revinfo;

alter table paikallinen_koulutuksenosa_tavoitteet
    add constraint FK_8b7y4roulklnottbhcvcejcej
    foreign key (tavoite_id)
    references lokalisoituteksti;

alter table paikallinen_koulutuksenosa_tavoitteet
    add constraint FK_apeljlvwy1tgr8y61a2vlvhxv
    foreign key (paikallinen_koulutuksenosa_id)
    references koulutuksenosan_paikallinen_tarkennus;

alter table paikallinen_koulutuksenosa_tavoitteet_AUD
    add constraint FK_ljy6vo22h3q34vqv3q31lfkei
    foreign key (REV)
    references revinfo;

alter table paikallinen_koulutuksenosa_tavoitteet_AUD
    add constraint FK_933modoynppkwqaph3yhmnssp
    foreign key (REVEND)
    references revinfo;
