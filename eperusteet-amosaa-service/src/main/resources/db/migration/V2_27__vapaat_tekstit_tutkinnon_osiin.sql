drop table tutkinnonosa_vapaa_teksti;

create table vapaa_teksti (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    nimi_id int8,
    teksti_id int8,
    primary key (id)
);

create table vapaa_teksti_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    nimi_id int8,
    teksti_id int8,
    primary key (id, REV)
);

create table tutkinnonosa_vapaa_teksti (
    tutkinnonosa_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    primary key (tutkinnonosa_id, jnro)
);

create table tutkinnonosa_vapaa_teksti_AUD (
    REV int4 not null,
    tutkinnonosa_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, tutkinnonosa_id, vapaat_id, jnro)
);

alter table tutkinnonosa_vapaa_teksti
    add constraint UK_jixp5t680vsa7b0ha71kxpdvo  unique (vapaat_id);

alter table tutkinnonosa_vapaa_teksti
    add constraint FK_jixp5t680vsa7b0ha71kxpdvo
    foreign key (vapaat_id)
    references vapaa_teksti;

alter table tutkinnonosa_vapaa_teksti
    add constraint FK_dpq4t5dwk6fygdleajxab621m
    foreign key (tutkinnonosa_id)
    references tutkinnonosa;

alter table tutkinnonosa_vapaa_teksti_AUD
    add constraint FK_k9gd86oix89xmpireuxncjft3
    foreign key (REV)
    references revinfo;

alter table tutkinnonosa_vapaa_teksti_AUD
    add constraint FK_34bhvu7reche6ypxc77byehsw
    foreign key (REVEND)
    references revinfo;

alter table vapaa_teksti
    add constraint FK_8kp0pvr8n05wnw5b2hsogp237
    foreign key (nimi_id)
    references lokalisoituteksti;

alter table vapaa_teksti
    add constraint FK_ao6ydfw0j2qkdnqwc1bovuqjb
    foreign key (teksti_id)
    references lokalisoituteksti;

alter table vapaa_teksti_AUD
    add constraint FK_l2mo3vaw6kpphkbg9wm9dedhl
    foreign key (REV)
    references revinfo;

alter table vapaa_teksti_AUD
    add constraint FK_shgcj6x9kdlckcth8tnc1dlrh
    foreign key (REVEND)
    references revinfo;
