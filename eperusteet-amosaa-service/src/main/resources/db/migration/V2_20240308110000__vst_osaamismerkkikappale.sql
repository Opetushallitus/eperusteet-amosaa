alter table opintokokonaisuus add column osaamismerkkiKappale_id int8;
alter table opintokokonaisuus_AUD add column osaamismerkkiKappale_id int8;

alter table sisaltoviite add column osaamismerkkiKappale_id int8;
alter table sisaltoviite_AUD add column osaamismerkkiKappale_id int8;

create table osaamismerkki_koodi (
                                     id int8 not null,
                                     koodi varchar(255),
                                     primary key (id)
);

create table osaamismerkki_koodi_AUD (
                                         id int8 not null,
                                         REV int4 not null,
                                         REVTYPE int2,
                                         REVEND int4,
                                         koodi varchar(255),
                                         primary key (id, REV)
);

create table osaamismerkkikappale (
                                      id int8 not null,
                                      luoja varchar(255),
                                      luotu timestamp,
                                      muokattu timestamp,
                                      muokkaaja varchar(255),
                                      kuvaus_id int8,
                                      primary key (id)
);

create table osaamismerkkikappale_AUD (
                                          id int8 not null,
                                          REV int4 not null,
                                          REVTYPE int2,
                                          REVEND int4,
                                          luoja varchar(255),
                                          luotu timestamp,
                                          muokattu timestamp,
                                          muokkaaja varchar(255),
                                          kuvaus_id int8,
                                          primary key (id, REV)
);

create table osaamismerkkikappale_osaamismerkki_koodi (
                                                          osaamismerkkikappale_id int8 not null,
                                                          osaamismerkkiKoodit_id int8 not null
);

create table osaamismerkkikappale_osaamismerkki_koodi_AUD (
                                                              REV int4 not null,
                                                              osaamismerkkikappale_id int8 not null,
                                                              osaamismerkkiKoodit_id int8 not null,
                                                              REVTYPE int2,
                                                              REVEND int4,
                                                              primary key (REV, osaamismerkkikappale_id, osaamismerkkiKoodit_id)
);

alter table opintokokonaisuus
    add constraint FK_rtj3f63mpdame2npnssj2dfy6
        foreign key (osaamismerkkiKappale_id)
            references osaamismerkkikappale;


alter table sisaltoviite
    add constraint FK_ppk7dj09cpyf4vsin51roswl3
        foreign key (osaamismerkkiKappale_id)
            references osaamismerkkikappale;


alter table osaamismerkki_koodi_AUD
    add constraint FK_cvv0g8y9qpnfangjy5b7u1496
        foreign key (REV)
            references revinfo;

alter table osaamismerkki_koodi_AUD
    add constraint FK_j4ejq5bwegbqa6cpo7jq5apxp
        foreign key (REVEND)
            references revinfo;

alter table osaamismerkkikappale
    add constraint FK_bewvvt86kcbcuh78cj1u8w2if
        foreign key (kuvaus_id)
            references lokalisoituteksti;

alter table osaamismerkkikappale_AUD
    add constraint FK_p9oylej211mwtqnfkogh0uhws
        foreign key (REV)
            references revinfo;

alter table osaamismerkkikappale_AUD
    add constraint FK_koaro3b5824q5fllcf7do23dh
        foreign key (REVEND)
            references revinfo;

alter table osaamismerkkikappale_osaamismerkki_koodi
    add constraint FK_1glv42k4j6wq4rrkkgbkpplnr
        foreign key (osaamismerkkiKoodit_id)
            references osaamismerkki_koodi;

alter table osaamismerkkikappale_osaamismerkki_koodi
    add constraint FK_231u7k2a6kouy4a3l73rn3j6v
        foreign key (osaamismerkkikappale_id)
            references osaamismerkkikappale;

alter table osaamismerkkikappale_osaamismerkki_koodi_AUD
    add constraint FK_qoiw24ro0rbyoctg40l6coka3
        foreign key (REV)
            references revinfo;

alter table osaamismerkkikappale_osaamismerkki_koodi_AUD
    add constraint FK_rwlu06par70i63k81twh2u4q9
        foreign key (REVEND)
            references revinfo;
