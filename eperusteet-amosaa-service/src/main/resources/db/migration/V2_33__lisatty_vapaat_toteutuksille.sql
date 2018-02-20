create table tutkinnonosa_toteutus_vapaa_teksti (
    tutkinnonosa_toteutus_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    primary key (tutkinnonosa_toteutus_id, jnro)
);

create table tutkinnonosa_toteutus_vapaa_teksti_AUD (
    REV int4 not null,
    tutkinnonosa_toteutus_id int8 not null,
    vapaat_id int8 not null,
    jnro int4 not null,
    REVTYPE int2,
    REVEND int4,
    primary key (REV, tutkinnonosa_toteutus_id, vapaat_id, jnro)
);