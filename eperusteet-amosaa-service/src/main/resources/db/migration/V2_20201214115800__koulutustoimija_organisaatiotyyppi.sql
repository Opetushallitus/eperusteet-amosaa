alter table koulutustoimija drop column organisaatioRyhma;

create table Koulutustoimija_organisaatioTyypit (
    Koulutustoimija_id int8 not null,
    organisaatioTyypit varchar(255)
);

create table Koulutustoimija_organisaatioTyypit_AUD (
    REV int4 not null,
    Koulutustoimija_id int8 not null,
    organisaatioTyypit varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Koulutustoimija_id, organisaatioTyypit)
);
