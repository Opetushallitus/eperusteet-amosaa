alter table sisaltoviite add column perusteenOsaId int8;
alter table sisaltoviite_aud add column perusteenOsaId int8;

alter table sisaltoviite add column kotoKielitaitotaso_id int8;
alter table sisaltoviite add column kotoLaajaAlainenOsaaminen_id int8;
alter table sisaltoviite add column kotoOpinto_id int8;

alter table sisaltoviite_aud add column kotoKielitaitotaso_id int8;
alter table sisaltoviite_aud add column kotoLaajaAlainenOsaaminen_id int8;
alter table sisaltoviite_aud add column kotoOpinto_id int8;

create table koto_kielitaitotaso (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    primary key (id)
);

create table koto_kielitaitotaso_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    primary key (id, REV)
);

create table koto_kielitaitotaso_laaja_alaiset_osaamiset (
    koto_kielitaitotaso_id int8 not null,
    laaja_alainen_osaaminen_id int8 not null
);

create table koto_kielitaitotaso_laaja_alaiset_osaamiset_AUD (
    REV int4 not null,
    koto_kielitaitotaso_id int8 not null,
    laaja_alainen_osaaminen_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, koto_kielitaitotaso_id, laaja_alainen_osaaminen_id)
);

create table koto_kielitaitotaso_taitotasot (
    koto_kielitaitotaso_id int8 not null,
    taitotaso_id int8 not null
);

create table koto_kielitaitotaso_taitotasot_AUD (
    REV int4 not null,
    koto_kielitaitotaso_id int8 not null,
    taitotaso_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, koto_kielitaitotaso_id, taitotaso_id)
);

create table koto_laajaalainenosaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    teksti_id int8,
    primary key (id)
);

create table koto_laajaalainenosaaminen_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    teksti_id int8,
    primary key (id, REV)
);

create table koto_opinto (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    primary key (id)
);

create table koto_opinto_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    primary key (id, REV)
);

create table koto_opinto_laaja_alaiset_osaamiset (
    koto_opinto_id int8 not null,
    laaja_alainen_osaaminen_id int8 not null
);

create table koto_opinto_laaja_alaiset_osaamiset_AUD (
    REV int4 not null,
    koto_opinto_id int8 not null,
    laaja_alainen_osaaminen_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, koto_opinto_id, laaja_alainen_osaaminen_id)
);

create table koto_opinto_taitotasot (
    koto_opinto_id int8 not null,
    taitotaso_id int8 not null
);

create table koto_opinto_taitotasot_AUD (
    REV int4 not null,
    koto_opinto_id int8 not null,
    taitotaso_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, koto_opinto_id, taitotaso_id)
);

create table koto_taitotaso (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    sisaltoTarkennus_id int8,
    tavoiteTarkennus_id int8,
    primary key (id)
);

create table koto_taitotaso_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    sisaltoTarkennus_id int8,
    tavoiteTarkennus_id int8,
    primary key (id, REV)
);

create table koto_taitotaso_laajaalainenosaaminen (
    id int8 not null,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    teksti_id int8,
    primary key (id)
);

create table koto_taitotaso_laajaalainenosaaminen_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    luoja varchar(255),
    luotu timestamp,
    muokattu timestamp,
    muokkaaja varchar(255),
    koodiUri varchar(255),
    teksti_id int8,
    primary key (id, REV)
);

alter table koto_kielitaitotaso_laaja_alaiset_osaamiset
    add constraint UK_3996njbuk3hdpic2mvsdlfhg7  unique (laaja_alainen_osaaminen_id);

alter table koto_kielitaitotaso_taitotasot
    add constraint UK_a9qe7qeac4tv3jkt3vyjjtils  unique (taitotaso_id);

alter table koto_opinto_laaja_alaiset_osaamiset
    add constraint UK_9rmkr3rehv3yayg1pg6xs29av  unique (laaja_alainen_osaaminen_id);

alter table koto_opinto_taitotasot
    add constraint UK_c0c5wefeu8efqcjtn1xs8lb5t  unique (taitotaso_id);

alter table koto_kielitaitotaso_AUD
    add constraint FK_vcd9pi6pqr575xvuo8yg47ew
    foreign key (REV)
    references revinfo;

alter table koto_kielitaitotaso_AUD
    add constraint FK_8inhbqigcgopeofxblyiuxylb
    foreign key (REVEND)
    references revinfo;

alter table koto_kielitaitotaso_laaja_alaiset_osaamiset
    add constraint FK_3996njbuk3hdpic2mvsdlfhg7
    foreign key (laaja_alainen_osaaminen_id)
    references koto_taitotaso_laajaalainenosaaminen;

alter table koto_kielitaitotaso_laaja_alaiset_osaamiset
    add constraint FK_obr3ftjj8c8ope1dvs830g15d
    foreign key (koto_kielitaitotaso_id)
    references koto_kielitaitotaso;

alter table koto_kielitaitotaso_laaja_alaiset_osaamiset_AUD
    add constraint FK_1itxyixepcackroicjg00bor0
    foreign key (REV)
    references revinfo;

alter table koto_kielitaitotaso_laaja_alaiset_osaamiset_AUD
    add constraint FK_9bpbu1rre01imo51uapd43isc
    foreign key (REVEND)
    references revinfo;

alter table koto_kielitaitotaso_taitotasot
    add constraint FK_a9qe7qeac4tv3jkt3vyjjtils
    foreign key (taitotaso_id)
    references koto_taitotaso;

alter table koto_kielitaitotaso_taitotasot
    add constraint FK_q598k0d0944yubib7jg294ms2
    foreign key (koto_kielitaitotaso_id)
    references koto_kielitaitotaso;

alter table koto_kielitaitotaso_taitotasot_AUD
    add constraint FK_dtwf2kjr6yv48wbo89suywjip
    foreign key (REV)
    references revinfo;

alter table koto_kielitaitotaso_taitotasot_AUD
    add constraint FK_egt7m1a3kymwmu94sg7yywus6
    foreign key (REVEND)
    references revinfo;

alter table koto_laajaalainenosaaminen
    add constraint FK_d35k9jat7f18n4whyxyskl2vi
    foreign key (teksti_id)
    references lokalisoituteksti;

alter table koto_laajaalainenosaaminen_AUD
    add constraint FK_dbp5r8mt9j1i3wkdcnepjf1ly
    foreign key (REV)
    references revinfo;

alter table koto_laajaalainenosaaminen_AUD
    add constraint FK_2mpqen6bxcf353dwjponei5k5
    foreign key (REVEND)
    references revinfo;

alter table koto_opinto_AUD
    add constraint FK_fr24bljagknujbhqxhivnxs8t
    foreign key (REV)
    references revinfo;

alter table koto_opinto_AUD
    add constraint FK_j864jx2kyftrenuf8m995k1tg
    foreign key (REVEND)
    references revinfo;

alter table koto_opinto_laaja_alaiset_osaamiset
    add constraint FK_9rmkr3rehv3yayg1pg6xs29av
    foreign key (laaja_alainen_osaaminen_id)
    references koto_taitotaso_laajaalainenosaaminen;

alter table koto_opinto_laaja_alaiset_osaamiset
    add constraint FK_63qsopx7tfy02xfq9qh6b00cl
    foreign key (koto_opinto_id)
    references koto_opinto;

alter table koto_opinto_laaja_alaiset_osaamiset_AUD
    add constraint FK_gaj21mtn9vs46r65ookdq0guj
    foreign key (REV)
    references revinfo;

alter table koto_opinto_laaja_alaiset_osaamiset_AUD
    add constraint FK_lhfn38o1cmwfiihup3wuun5w6
    foreign key (REVEND)
    references revinfo;

alter table koto_opinto_taitotasot
    add constraint FK_c0c5wefeu8efqcjtn1xs8lb5t
    foreign key (taitotaso_id)
    references koto_taitotaso;

alter table koto_opinto_taitotasot
    add constraint FK_po9n0yxyegje6x5n05yhnynfy
    foreign key (koto_opinto_id)
    references koto_opinto;

alter table koto_opinto_taitotasot_AUD
    add constraint FK_ds5x6fghxmryve5lj0co73qp5
    foreign key (REV)
    references revinfo;

alter table koto_opinto_taitotasot_AUD
    add constraint FK_f10weg1vpqhin2464pnci1dqr
    foreign key (REVEND)
    references revinfo;

alter table koto_taitotaso
    add constraint FK_j0sxeknrjchj8t8g6rfkib7ff
    foreign key (sisaltoTarkennus_id)
    references lokalisoituteksti;

alter table koto_taitotaso
    add constraint FK_56dqw6bwwa4bsxtowijn4kqgb
    foreign key (tavoiteTarkennus_id)
    references lokalisoituteksti;

alter table koto_taitotaso_AUD
    add constraint FK_blweue3jxr3ryd1g4jp1qjldq
    foreign key (REV)
    references revinfo;

alter table koto_taitotaso_AUD
    add constraint FK_a52vjvwbe5p5v2lf6pb8jm5s0
    foreign key (REVEND)
    references revinfo;

alter table koto_taitotaso_laajaalainenosaaminen
    add constraint FK_dgjvvhil8agge5vckqsj3y0o
    foreign key (teksti_id)
    references lokalisoituteksti;

alter table koto_taitotaso_laajaalainenosaaminen_AUD
    add constraint FK_grqofainm7dwr2i9ku2u60qns
    foreign key (REV)
    references revinfo;

alter table koto_taitotaso_laajaalainenosaaminen_AUD
    add constraint FK_3tckrvmj3v7upyh8rc3x12i1m
    foreign key (REVEND)
    references revinfo;
