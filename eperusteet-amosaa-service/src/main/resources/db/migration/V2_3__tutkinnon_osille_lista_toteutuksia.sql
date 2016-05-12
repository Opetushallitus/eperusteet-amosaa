CREATE TABLE tutkinnonosa_toteutus (
    id BIGINT PRIMARY KEY,
    jnro INTEGER,
    otsikko_id BIGINT REFERENCES lokalisoituteksti(id),
    tutkinnonosa_id BIGINT REFERENCES tutkinnonosa(id),
    tavatjaymparisto_id BIGINT REFERENCES tekstiosa(id),
    arvioinnista_id BIGINT REFERENCES tekstiosa(id),
    osaamisalaKoodi VARCHAR(255),
    oppiaineKoodi VARCHAR(255),
    kurssiKoodi VARCHAR(255),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255)
);

CREATE TABLE tutkinnonosa_toteutus_aud (
    id BIGINT,
    jnro INTEGER,
    otsikko_id BIGINT,
    tutkinnonosa_id BIGINT,
    tavatjaymparisto_id BIGINT,
    arvioinnista_id BIGINT,
    osaamisalaKoodi VARCHAR(255),
    oppiaineKoodi VARCHAR(255),
    kurssiKoodi VARCHAR(255),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    PRIMARY KEY (id, rev)
);

ALTER TABLE tutkinnonosa DROP COLUMN arvioinnista_id;
ALTER TABLE tutkinnonosa DROP COLUMN tavatjaymparisto_id;
ALTER TABLE tutkinnonosa_aud DROP COLUMN arvioinnista_id;
ALTER TABLE tutkinnonosa_aud DROP COLUMN tavatjaymparisto_id;