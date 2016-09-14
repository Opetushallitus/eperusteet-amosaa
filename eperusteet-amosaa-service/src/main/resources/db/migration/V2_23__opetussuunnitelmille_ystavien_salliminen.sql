ALTER TABLE koulutustoimija ADD COLUMN salliystavat BOOLEAN DEFAULT FALSE;
ALTER TABLE koulutustoimija_aud ADD COLUMN salliystavat BOOLEAN DEFAULT FALSE;

DROP TABLE koulutustoimija_ystavat;
DROP TABLE koulutustoimija_ystavat_aud;

create table koulutustoimija_koulutustoimija (
    Koulutustoimija_id int8 not null references koulutustoimija(id),
    ystavat_id int8 not null references koulutustoimija(id),
    primary key (Koulutustoimija_id, ystavat_id)
);

create table koulutustoimija_koulutustoimija_AUD (
    REV int4 not null,
    Koulutustoimija_id int8 not null,
    ystavat_id int8 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, Koulutustoimija_id, ystavat_id)
);
