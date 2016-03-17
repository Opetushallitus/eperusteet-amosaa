CREATE SEQUENCE hibernate_sequence;

CREATE TABLE revinfo (
    rev integer NOT NULL PRIMARY KEY,
    revtstmp bigint,
    kommentti character varying(1000),
    muokkaajaoid character varying(255)
);

CREATE TABLE lukko (
    id bigint NOT NULL PRIMARY KEY,
    haltija_oid character varying(255),
    luotu timestamp without time zone,
    vanhenemisaika integer NOT NULL
);

CREATE TABLE kommentti (
    id bigint NOT NULL PRIMARY KEY,
    luoja character varying(255),
    muokattu timestamp without time zone,
    luotu timestamp without time zone,
    muokkaaja character varying(255),
    nimi character varying(255),
    parentid bigint,
    ylinid bigint,
    poistettu boolean,
    sisalto character varying(1024)
);

CREATE TABLE koodistokoodi (
    id bigint NOT NULL PRIMARY KEY,
    koodiarvo character varying(255),
    koodiuri character varying(255)
);

CREATE TABLE liite (
    id uuid NOT NULL PRIMARY KEY,
    data oid NOT NULL,
    luotu timestamp without time zone,
    nimi character varying(1024),
    tyyppi character varying(255) NOT NULL
);

CREATE TABLE lokalisoituteksti (
    id bigint NOT NULL PRIMARY KEY
);

CREATE TABLE lokalisoituteksti_teksti (
    lokalisoituteksti_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kieli character varying(255),
    teksti text
);

CREATE TABLE ohje (
    id bigint NOT NULL PRIMARY KEY,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id bigint REFERENCES lokalisoituteksti(id),
    tyyppi character varying(255) NOT NULL
);

CREATE TABLE ohje_aud (
    id bigint NOT NULL,
    rev integer NOT NULL REFERENCES revinfo(rev),
    revtype smallint,
    revend integer REFERENCES revinfo(rev),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id bigint,
    tyyppi character varying(255),
    PRIMARY KEY(id, rev)
);

CREATE TABLE tekstikappale (
    id bigint NOT NULL PRIMARY KEY,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255) NOT NULL,
    nimi_id bigint REFERENCES lokalisoituteksti(id),
    teksti_id bigint REFERENCES lokalisoituteksti(id),
    tunniste uuid,
    pakollinen boolean,
    valmis boolean
);

CREATE TABLE tekstikappale_aud (
    id bigint NOT NULL,
    rev integer NOT NULL REFERENCES revinfo(rev),
    revtype smallint,
    revend integer REFERENCES revinfo(rev),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255),
    nimi_id bigint,
    teksti_id bigint,
    tunniste uuid,
    pakollinen boolean,
    valmis boolean,
    PRIMARY KEY(id, rev)
);

CREATE TABLE tekstiosa (
    id bigint NOT NULL PRIMARY KEY,
    otsikko_id bigint REFERENCES lokalisoituteksti(id),
    teksti_id bigint REFERENCES lokalisoituteksti(id)
);

CREATE TABLE tekstiosa_aud (
    id bigint NOT NULL,
    rev integer NOT NULL REFERENCES revinfo(rev),
    revtype smallint,
    revend integer REFERENCES revinfo(rev),
    otsikko_id bigint,
    teksti_id bigint,
    PRIMARY KEY(id, rev)
);

CREATE TABLE opetussuunnitelmat (
    id bigint NOT NULL PRIMARY KEY,
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    tekstit_id bigint NOT NULL,
    paatosnumero CHARACTER VARYING(255) UNIQUE,
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    tila CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE opetussuunnitelmat_aud (
    id bigint NOT NULL PRIMARY KEY,
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    tekstit_id bigint NOT NULL,
    paatosnumero CHARACTER VARYING(255) UNIQUE,
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    tila CHARACTER VARYING(255) NOT NULL,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE yhteiset (
    id bigint NOT NULL PRIMARY KEY,
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id bigint REFERENCES lokalisoituteksti(id),
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    tekstit_id bigint NOT NULL,
    luoja CHARACTER VARYING(255),
    paatosnumero CHARACTER VARYING(255) UNIQUE,
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    tila CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE yhteiset_aud (
    id bigint NOT NULL,
    nimi_id bigint NOT NULL,
    kuvaus_id bigint,
    tila CHARACTER VARYING(255) NOT NULL,
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    tekstit_id bigint,
    paatosnumero CHARACTER VARYING(255),
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    hyvaksymispaiva TIMESTAMP WITHOUT TIME ZONE,
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
    yhteiset_id bigint NOT NULL,
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
    yhteiset_id bigint NOT NULL,
    nimi_id bigint NOT NULL,
    kuvaus_id bigint,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE tekstikappaleviite (
    id bigint NOT NULL PRIMARY KEY,
    yhteiset_id bigint,
    tekstikappale_id bigint REFERENCES tekstikappale(id),
    owner bigint,
    vanhempi_id bigint,
    lapset_order integer,
    pakollinen boolean,
    valmis boolean,
    UNIQUE (id, owner)
);

CREATE TABLE tekstikappaleviite_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    yhteiset_id bigint,
    tekstikappale_id bigint,
    vanhempi_id bigint,
    pakollinen boolean,
    owner bigint,
    valmis boolean,
    PRIMARY KEY (id, rev)
);

ALTER TABLE yhteiset ADD CONSTRAINT yhteiset_tekstikappaleviite_tekstit_viite FOREIGN KEY (tekstit_id) REFERENCES tekstikappaleviite(id);

CREATE TABLE kayttaja (
    id bigint NOT NULL PRIMARY KEY,
    tiedotekuittaus TIMESTAMP NOT NULL,
    oid VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE kayttaja_oikeudet (
    id bigint NOT NULL PRIMARY KEY,
    kayttaja_id bigint REFERENCES kayttaja(id),
    koulutustoimija_id bigint REFERENCES koulutustoimija(id),
    yhteiset_id bigint REFERENCES yhteiset(id),
    opetussuunnitelma_id bigint REFERENCES opetussuunnitelmat(id),
    oikeus VARCHAR(64) NOT NULL,
    UNIQUE(kayttaja_id, koulutustoimija_id, yhteiset_id),
    UNIQUE(kayttaja_id, koulutustoimija_id, opetussuunnitelma_id),
    CHECK((yhteiset_id IS NULL AND opetussuunnitelma_id IS NOT NULL)
        OR (yhteiset_id IS NOT NULL AND opetussuunnitelma_id IS NULL))
);

CREATE TABLE yhteiset_liite (
    yhteiset_id BIGINT NOT NULL,
    liite_id UUID NOT NULL,
    PRIMARY KEY (yhteiset_id, liite_id)
);

CREATE TABLE yhteiset_liite_aud (
    yhteiset_id BIGINT NOT NULL,
    liite_id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (rev, yhteiset_id, liite_id)
);

CREATE TABLE termi (
    id bigint NOT NULL PRIMARY KEY,
    koulutustoimija_id BIGINT NOT NULL REFERENCES koulutustoimija(id),
    termi_id BIGINT REFERENCES lokalisoituteksti(id),
    selitys_id BIGINT REFERENCES lokalisoituteksti(id),
    avain TEXT NOT NULL UNIQUE,
    alaviite BOOLEAN
);

CREATE TABLE termi_aud (
    id BIGINT NOT NULL,
    koulutustoimija_id BIGINT,
    termi_id BIGINT,
    selitys_id BIGINT,
    avain TEXT,
    alaviite BOOLEAN,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    PRIMARY KEY (id, rev)
);

CREATE TABLE poistetut (
    id bigint NOT NULL PRIMARY KEY,
    nimi_id bigint NOT NULL REFERENCES lokalisoituteksti(id),
    yhteiset_id bigint REFERENCES yhteiset(id),
    koulutustoimija_id bigint REFERENCES koulutustoimija(id),
--     opetussuunnitelma_id bigint REFERENCES opetussuunnitelma,
    poistettu_id bigint NOT NULL,
    pvm timestamp without time zone,
    tyyppi VARCHAR(64),
    muokkaajaoid character varying(255),
    UNIQUE(id)
);