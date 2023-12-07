ALTER TABLE omaosaalue ADD COLUMN paikallinenTarkennus_id int8;
ALTER TABLE omaosaalue_AUD ADD COLUMN paikallinenTarkennus_id int8;

alter table omaosaalue
    add constraint FK_89lvfoqubke35oje74v009loi
        foreign key (paikallinenTarkennus_id)
            references lokalisoituteksti;

create table omaosaalue_vapaa_teksti (
                                         omaosaalue_id int8 not null,
                                         vapaat_id int8 not null,
                                         jnro int4 not null,
                                         primary key (omaosaalue_id, jnro)
);

create table omaosaalue_vapaa_teksti_AUD (
                                             REV int4 not null,
                                             omaosaalue_id int8 not null,
                                             vapaat_id int8 not null,
                                             jnro int4 not null,
                                             REVTYPE int2,
                                             REVEND int4,
                                             primary key (REV, omaosaalue_id, vapaat_id, jnro)
);


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
