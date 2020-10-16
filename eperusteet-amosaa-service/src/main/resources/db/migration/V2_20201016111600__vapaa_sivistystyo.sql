alter table sisaltoviite add column opintokokonaisuus_id int8;

alter table sisaltoviite_aud add column opintokokonaisuus_id int8;

create table opintokokonaisuus (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    minimilaajuus int4,
    nimiKoodi varchar(255),
    arvioinninKuvaus_id int8,
    keskeisetSisallot_id int8,
    kuvaus_id int8,
    opetuksenTavoiteOtsikko_id int8,
    primary key (id)
);

create table opintokokonaisuus_arviointi (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteesta boolean,
    arviointi_id int8,
    primary key (id)
);

create table opintokokonaisuus_arviointi_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteesta boolean,
    arviointi_id int8,
    primary key (id, REV)
);

create table opintokokonaisuus_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    minimilaajuus int4,
    nimiKoodi varchar(255),
    arvioinninKuvaus_id int8,
    keskeisetSisallot_id int8,
    kuvaus_id int8,
    opetuksenTavoiteOtsikko_id int8,
    primary key (id, REV)
);

create table opintokokonaisuus_opintokokonaisuus_arviointi (
    Opintokokonaisuus_id int8 not null,
    arvioinnit_id int8 not null,
    arvioinnit_ORDER int4 not null,
    primary key (Opintokokonaisuus_id, arvioinnit_ORDER)
);

create table opintokokonaisuus_opintokokonaisuus_arviointi_AUD (
    REV int4 not null,
    Opintokokonaisuus_id int8 not null,
    arvioinnit_id int8 not null,
    arvioinnit_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Opintokokonaisuus_id, arvioinnit_id, arvioinnit_ORDER)
);

create table opintokokonaisuus_opintokokonaisuus_tavoite (
    Opintokokonaisuus_id int8 not null,
    tavoitteet_id int8 not null,
    tavoitteet_ORDER int4 not null,
    primary key (Opintokokonaisuus_id, tavoitteet_ORDER)
);

create table opintokokonaisuus_opintokokonaisuus_tavoite_AUD (
    REV int4 not null,
    Opintokokonaisuus_id int8 not null,
    tavoitteet_id int8 not null,
    tavoitteet_ORDER int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Opintokokonaisuus_id, tavoitteet_id, tavoitteet_ORDER)
);

create table opintokokonaisuus_tavoite (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteesta boolean,
    tavoite_koodi varchar(255),
    primary key (id)
);

create table opintokokonaisuus_tavoite_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    perusteesta boolean,
    tavoite_koodi varchar(255),
    primary key (id, REV)
);

alter table opintokokonaisuus_opintokokonaisuus_arviointi
    add constraint UK_n0vggrkvgnbdn9kfiqf0ac8cl unique (arvioinnit_id);

alter table opintokokonaisuus_opintokokonaisuus_tavoite
    add constraint UK_2t48omwaofqnoyjdgig8sdu7e unique (tavoitteet_id);

alter table opintokokonaisuus
    add constraint FKe8brb0xb0k8vekyxo2cwe4e2q
    foreign key (arvioinninKuvaus_id)
    references lokalisoituteksti;

alter table opintokokonaisuus
    add constraint FK2wcvpnpp820gydvdgcxd820i0
    foreign key (keskeisetSisallot_id)
    references lokalisoituteksti;

alter table opintokokonaisuus
    add constraint FK1ghl8huk4a4mihprd8tjtlsxr
    foreign key (kuvaus_id)
    references lokalisoituteksti;

alter table opintokokonaisuus
    add constraint FKt1o6nvhemhrp7q2wde8rna7le
    foreign key (opetuksenTavoiteOtsikko_id)
    references lokalisoituteksti;

alter table opintokokonaisuus_arviointi
    add constraint FKk6a1942enhcv4amme85ujv6hj
    foreign key (arviointi_id)
    references lokalisoituteksti;

alter table opintokokonaisuus_arviointi_AUD
    add constraint FKfjk0nrlu8wanf8jim7a2ftqxu
    foreign key (REV)
    references revinfo;

alter table opintokokonaisuus_arviointi_AUD
    add constraint FK5ymulwa0tny5625b16ng9cx1i
    foreign key (REVEND)
    references revinfo;

alter table opintokokonaisuus_AUD
    add constraint FKhjnw967khkqoyyvphyf9w80i6
    foreign key (REV)
    references revinfo;

alter table opintokokonaisuus_AUD
    add constraint FK87s3udisjhmsi1xm4dxaojefb
    foreign key (REVEND)
    references revinfo;

alter table opintokokonaisuus_opintokokonaisuus_arviointi
    add constraint FKcrlwidajw7alvmu6s8q9m4t9a
    foreign key (arvioinnit_id)
    references opintokokonaisuus_arviointi;

alter table opintokokonaisuus_opintokokonaisuus_arviointi
    add constraint FKcebqn31sq31gr8xcadwcbkipj
    foreign key (Opintokokonaisuus_id)
    references opintokokonaisuus;

alter table opintokokonaisuus_opintokokonaisuus_arviointi_AUD
    add constraint FKhvudcnvo789c1qlh4k1itfryg
    foreign key (REV)
    references revinfo;

alter table opintokokonaisuus_opintokokonaisuus_arviointi_AUD
    add constraint FKt1b3uhuwcculdjf49jd3ljf2v
    foreign key (REVEND)
    references revinfo;

alter table opintokokonaisuus_opintokokonaisuus_tavoite
    add constraint FKlh6g61416oe5ukc2ho4m6262r
    foreign key (tavoitteet_id)
    references opintokokonaisuus_tavoite;

alter table opintokokonaisuus_opintokokonaisuus_tavoite
    add constraint FKhinqtij1oiov4vptybjcrrbai
    foreign key (Opintokokonaisuus_id)
    references opintokokonaisuus;

alter table opintokokonaisuus_opintokokonaisuus_tavoite_AUD
    add constraint FK79ymbim5m5nd9mxne0vejvjex
    foreign key (REV)
    references revinfo;

alter table opintokokonaisuus_opintokokonaisuus_tavoite_AUD
    add constraint FK23hsmtbrx8trfo0ui3yi1qjrm
    foreign key (REVEND)
    references revinfo;

alter table opintokokonaisuus_tavoite_AUD
    add constraint FK89dslcrp14q9c8ne8qlkyd331
    foreign key (REV)
    references revinfo;

alter table opintokokonaisuus_tavoite_AUD
    add constraint FK4hqqnr6p2ygs96wuy0sn58a2e
    foreign key (REVEND)
    references revinfo;

alter table sisaltoviite
    add constraint FK9vawgf84oj2s91nkk4u3ii42e
    foreign key (opintokokonaisuus_id)
    references opintokokonaisuus;
