CREATE SEQUENCE hibernate_sequence;

CREATE TABLE revinfo (
    rev INTEGER NOT NULL PRIMARY KEY,
    revtstmp BIGINT,
    kommentti character varying(1000),
    muokkaajaoid character varying(255)
);

CREATE TABLE lukko (
    id BIGINT NOT NULL PRIMARY KEY,
    haltija_oid character varying(255),
    luotu timestamp without time zone,
    vanhenemisaika INTEGER NOT NULL
);

CREATE TABLE kommentti (
    id BIGINT NOT NULL PRIMARY KEY,
    luoja character varying(255),
    muokattu timestamp without time zone,
    luotu timestamp without time zone,
    muokkaaja character varying(255),
    nimi character varying(255),
    parentid BIGINT,
    ylinid BIGINT,
    poistettu BOOLEAN,
    sisalto character varying(1024)
);

CREATE TABLE koodistokoodi (
    id BIGINT NOT NULL PRIMARY KEY,
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
    id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE lokalisoituteksti_teksti (
    lokalisoituteksti_id BIGINT NOT NULL REFERENCES lokalisoituteksti(id),
    kieli character varying(255),
    teksti text
);

CREATE TABLE ohje (
    id BIGINT NOT NULL PRIMARY KEY,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id BIGINT REFERENCES lokalisoituteksti(id),
    tyyppi character varying(255) NOT NULL
);

CREATE TABLE ohje_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id BIGINT,
    tyyppi character varying(255),
    PRIMARY KEY(id, rev)
);

CREATE TABLE tekstikappale (
    id BIGINT NOT NULL PRIMARY KEY,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255) NOT NULL,
    nimi_id BIGINT REFERENCES lokalisoituteksti(id),
    teksti_id BIGINT REFERENCES lokalisoituteksti(id),
    tunniste uuid,
    pakollinen BOOLEAN,
    valmis BOOLEAN
);

CREATE TABLE tekstikappale_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255),
    nimi_id BIGINT,
    teksti_id BIGINT,
    tunniste uuid,
    pakollinen BOOLEAN,
    valmis BOOLEAN,
    PRIMARY KEY(id, rev)
);

CREATE TABLE tekstiosa (
    id BIGINT NOT NULL PRIMARY KEY,
    otsikko_id BIGINT REFERENCES lokalisoituteksti(id),
    teksti_id BIGINT REFERENCES lokalisoituteksti(id)
);

CREATE TABLE tekstiosa_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo(rev),
    otsikko_id BIGINT,
    teksti_id BIGINT,
    PRIMARY KEY(id, rev)
);

CREATE TABLE koulutustoimija (
    id BIGINT NOT NULL PRIMARY KEY,
    organisaatio VARCHAR(255) UNIQUE NOT NULL CHECK (organisaatio <> ''),
    nimi_id BIGINT NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id BIGINT REFERENCES lokalisoituteksti(id),
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255)
);

CREATE TABLE koulutustoimija_aud (
    id BIGINT NOT NULL PRIMARY KEY,
    organisaatio VARCHAR(255) UNIQUE NOT NULL CHECK (organisaatio <> ''),
    nimi_id BIGINT NOT NULL,
    kuvaus_id BIGINT,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE opetussuunnitelma (
    id BIGINT NOT NULL PRIMARY KEY,
    nimi_id BIGINT NOT NULL REFERENCES lokalisoituteksti(id),
    kuvaus_id BIGINT REFERENCES lokalisoituteksti(id),
    koulutustoimija_id BIGINT NOT NULL REFERENCES koulutustoimija(id),
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    pohja_id BIGINT REFERENCES opetussuunnitelma(id),
    peruste CHARACTER VARYING(255),
    luoja CHARACTER VARYING(255),
    paatosnumero CHARACTER VARYING(255) UNIQUE,
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    tila CHARACTER VARYING(255) NOT NULL,
    tyyppi CHARACTER VARYING(255) NOT NULL,
    CHECK ((tyyppi = 'OPS' AND peruste IS NOT NULL)
        OR (tyyppi = 'YHTEINEN' AND pohja_id IS NOT NULL)
        OR (tyyppi = 'KOOSTE' OR tyyppi = 'POHJA'))
);

CREATE TABLE opetussuunnitelma_aud (
    id BIGINT NOT NULL,
    nimi_id BIGINT NOT NULL,
    kuvaus_id BIGINT,
    koulutustoimija_id BIGINT,
    tila CHARACTER VARYING(255) NOT NULL,
    esikatseltavissa BOOLEAN,
    paatospaivamaara TIMESTAMP WITHOUT TIME ZONE,
    pohja_id BIGINT REFERENCES opetussuunnitelma(id),
    peruste CHARACTER VARYING(255),
    paatosnumero CHARACTER VARYING(255),
    hyvaksyja CHARACTER VARYING(255),
    voimaantulo TIMESTAMP WITHOUT TIME ZONE,
    hyvaksymispaiva TIMESTAMP WITHOUT TIME ZONE,
    tyyppi CHARACTER VARYING(255) NOT NULL,
    luoja CHARACTER VARYING(255),
    luotu TIMESTAMP WITHOUT TIME ZONE,
    muokattu TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja CHARACTER VARYING(255),
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE opetussuunnitelma_julkaisukielet (
    opetussuunnitelma_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    julkaisukielet CHARACTER VARYING(255)
);

CREATE TABLE opetussuunnitelma_julkaisukielet_aud (
    opetussuunnitelma_id BIGINT NOT NULL,
    julkaisukielet VARCHAR(255) NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER
);

CREATE TABLE tekstikappaleviite (
    id BIGINT NOT NULL PRIMARY KEY,
    tekstikappale_id BIGINT REFERENCES tekstikappale(id),
    owner_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    vanhempi_id BIGINT,
    lapset_order INTEGER,
    pakollinen BOOLEAN,
    valmis BOOLEAN
);

CREATE TABLE tekstikappaleviite_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    tekstikappale_id BIGINT,
    vanhempi_id BIGINT,
    pakollinen BOOLEAN,
    owner_id BIGINT,
    valmis BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE kayttaja (
    id BIGINT NOT NULL PRIMARY KEY,
    oid VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE kayttaja_oikeudet (
    id BIGINT NOT NULL PRIMARY KEY,
    kayttaja_id BIGINT REFERENCES kayttaja(id),
    koulutustoimija_id BIGINT REFERENCES koulutustoimija(id),
    opetussuunnitelma_id BIGINT REFERENCES opetussuunnitelma(id),
    oikeus VARCHAR(64) NOT NULL,
    UNIQUE(kayttaja_id, koulutustoimija_id, opetussuunnitelma_id)
);

CREATE TABLE opetussuunnitelma_liite (
    opetussuunnitelma_id BIGINT NOT NULL,
    liite_id UUID NOT NULL,
    PRIMARY KEY (opetussuunnitelma_id, liite_id)
);

CREATE TABLE opetussuunnitelma_liite_aud (
    opetussuunnitelma_id BIGINT NOT NULL,
    liite_id uuid NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (rev, opetussuunnitelma_id, liite_id)
);

CREATE TABLE termi (
    id BIGINT NOT NULL PRIMARY KEY,
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
    id BIGINT NOT NULL PRIMARY KEY,
    nimi_id BIGINT NOT NULL REFERENCES lokalisoituteksti(id),
    opetussuunnitelma_id BIGINT REFERENCES opetussuunnitelma(id),
    koulutustoimija_id BIGINT REFERENCES koulutustoimija(id),
    poistettu_id BIGINT NOT NULL,
    pvm TIMESTAMP WITHOUT TIME ZONE,
    tyyppi VARCHAR(64),
    muokkaajaoid CHARACTER VARYING(255),
    UNIQUE(id)
);

create table dokumentti (
    id BIGINT NOT NULL PRIMARY KEY,
    opetussuunnitelma_id BIGINT NOT NULL REFERENCES opetussuunnitelma(id),
    kieli CHARACTER VARYING NOT NULL,
    luoja CHARACTER VARYING,
    aloitusaika TIMESTAMP WITHOUT TIME ZONE,
    valmistumisaika TIMESTAMP WITHOUT TIME ZONE,
    tila CHARACTER VARYING NOT NULL,
    edistyminen CHARACTER VARYING NOT NULL,
    dokumenttidata OID,
    kansikuva OID,
    ylatunniste OID,
    alatunniste OID,
    virhekoodi TEXT
);

CREATE TABLE tiedote (
    id                 BIGINT NOT NULL PRIMARY KEY,
    koulutustoimija_id BIGINT NOT NULL REFERENCES koulutustoimija (id),
    otsikko            TEXT,
    teksti             TEXT,
    julkinen           BOOLEAN,
    tarkea             BOOLEAN,
    luoja              CHARACTER VARYING(255),
    luotu              TIMESTAMP WITHOUT TIME ZONE,
    muokkaaja          CHARACTER VARYING(255),
    muokattu           TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE koulutustoimijakayttaja (
    id BIGINT NOT NULL PRIMARY KEY,
    koulutustoimija_id BIGINT NOT NULL REFERENCES koulutustoimija(id),
    kayttaja_id BIGINT NOT NULL REFERENCES kayttaja(id),
    UNIQUE (kayttaja_id, koulutustoimija_id)
);

CREATE TABLE kayttaja_kuittaus (
    id BIGINT NOT NULL PRIMARY KEY,
    kayttaja BIGINT NOT NULL,
    tiedote BIGINT NOT NULL,
    UNIQUE(kayttaja, tiedote)
);