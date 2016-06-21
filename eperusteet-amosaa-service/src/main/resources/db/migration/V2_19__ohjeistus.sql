DROP TABLE ohje;
DROP TABLE ohje_aud;

create table Ohje (
    id int8 not null,
    kysymys_id int8,
    vastaus_id int8,
    primary key (id)
);

create table Ohje_AUD (
    id int8 not null,
    REV int4 not null,
    REVTYPE int2,
    REVEND int4,
    kysymys_id int8,
    vastaus_id int8,
    primary key (id, REV)
);

alter table Ohje
    add constraint FK_gxer65l72hocucn9hjfsm2k68
    foreign key (kysymys_id)
    references lokalisoituteksti;

alter table Ohje
    add constraint FK_rem9ym6ut7a7pwcrl3vjloyw
    foreign key (vastaus_id)
    references lokalisoituteksti;

alter table Ohje_AUD
    add constraint FK_akf2els4wqblr4bfhlim7n278
    foreign key (REV)
    references revinfo;

alter table Ohje_AUD
    add constraint FK_3pgl44h9b9emqd05euh6jo47l
    foreign key (REVEND)
    references revinfo;
