CREATE TABLE yhteiset (
    id bigint NOT NULL PRIMARY KEY,
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    tila CHARACTER VARYING(255) NOT NULL,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255)
);

CREATE TABLE yhteiset_aud (
    id bigint NOT NULL,
    nimi_id bigint NOT NULL,
    kuvaus_id bigint,
    tila CHARACTER VARYING(255) NOT NULL,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE yhteiset_julkaisukielet (
    yhteiset_id bigint NOT NULL REFERENCES yhteiset(id),
    julkaisukielet CHARACTER VARYING(255)
);

CREATE TABLE yhteiset_julkaisukielet_aud (
    yhteiset_id bigint NOT NULL,
    julkaisukielet VARCHAR(255) NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE koulutustoimija (
    id bigint NOT NULL PRIMARY KEY,
    organisaatio VARCHAR(255) UNIQUE NOT NULL CHECK (organisaatio <> ''),
    yhteiset_id bigint NOT NULL REFERENCES yhteiset(id),
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255)
);

CREATE TABLE koulutustoimija_aud (
    id bigint NOT NULL PRIMARY KEY,
    organisaatio VARCHAR(255) UNIQUE NOT NULL CHECK (organisaatio <> ''),
    yhteiset_id bigint NOT NULL REFERENCES yhteiset(id),
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE kayttaja (
    id bigint NOT NULL PRIMARY KEY,
    tiedotekuittaus TIMESTAMP NOT NULL,
    oid VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE kayttaja_oikeudet (
    id bigint NOT NULL PRIMARY KEY,
    kayttaja_id bigint REFERENCES kayttaja(id),
    ops_id bigint REFERENCES opetussuunnitelma(id),
    koulutustoimija_id bigint REFERENCES koulutustoimija(id),
    oikeus VARCHAR(64) NOT NULL,
    UNIQUE(kayttaja_id, ops_id, koulutustoimija_id)
);
