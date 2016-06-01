create table SuorituspolkuRivi_koodit (
    SuorituspolkuRivi_id int8 not null references suorituspolku_rivi(id),
    koodit varchar(255)
);

create table SuorituspolkuRivi_koodit_AUD (
    REV int4 not null,
    SuorituspolkuRivi_id int8 not null,
    koodit varchar(255) not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, SuorituspolkuRivi_id, koodit)
);


ALTER TABLE suorituspolku_rivi ADD COLUMN piilotettu boolean;
ALTER TABLE suorituspolku_rivi ADD COLUMN kuvaus_id bigint REFERENCES lokalisoituteksti(id);

ALTER TABLE suorituspolku_rivi_aud ADD COLUMN piilotettu boolean;
ALTER TABLE suorituspolku_rivi_aud ADD COLUMN kuvaus_id bigint;

UPDATE suorituspolku_rivi SET piilotettu = true;