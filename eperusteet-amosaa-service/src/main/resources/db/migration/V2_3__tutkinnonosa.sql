-- Arviointi

CREATE TABLE arviointi (
  id BIGINT PRIMARY KEY NOT NULL,
  lisatiedot_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE arviointi_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  lisatiedot_id BIGINT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE arvioinninkohdealue (
  id BIGINT PRIMARY KEY NOT NULL,
  otsikko_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE arviointi_arvioinninkohdealue (
  arviointi_id BIGINT NOT NULL REFERENCES arviointi (id),
  arvioinninkohdealue_id BIGINT NOT NULL REFERENCES arvioinninkohdealue (id),
  arvioinnin_kohdealueet_order INTEGER NOT NULL,
  PRIMARY KEY (arviointi_id, arvioinnin_kohdealueet_order),
  UNIQUE (arvioinninkohdealue_id)
);

CREATE TABLE arviointi_arvioinninkohdealue_aud (
  arviointi_id BIGINT NOT NULL,
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinnin_kohdealueet_order INTEGER NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (arviointi_id, arvioinninkohdealue_id, arvioinnin_kohdealueet_order, rev)
);

CREATE TABLE arviointiasteikko (
  id BIGINT PRIMARY KEY NOT NULL
);

CREATE TABLE osaamistaso (
  id BIGINT PRIMARY KEY NOT NULL,
  otsikko_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE arviointiasteikko_osaamistaso (
  arviointiasteikko_id BIGINT NOT NULL REFERENCES arviointiasteikko (id),
  osaamistasot_id BIGINT NOT NULL REFERENCES osaamistaso (id),
  osaamistasot_order INTEGER NOT NULL,
  PRIMARY KEY (arviointiasteikko_id, osaamistasot_order),
  UNIQUE (osaamistasot_id)
);

CREATE TABLE osaamistasonkriteeri (
  id BIGINT PRIMARY KEY NOT NULL,
  osaamistaso_id BIGINT REFERENCES osaamistaso (id)
);

CREATE TABLE osaamistasonkriteeri_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  osaamistaso_id BIGINT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE osaamistasonkriteeri_tekstipalanen (
  osaamistasonkriteeri_id BIGINT NOT NULL REFERENCES osaamistasonkriteeri (id),
  tekstipalanen_id BIGINT NOT NULL REFERENCES lokalisoitu_teksti (id),
  kriteerit_order INTEGER NOT NULL,
  PRIMARY KEY (osaamistasonkriteeri_id, kriteerit_order)
);

CREATE TABLE osaamistasonkriteeri_tekstipalanen_aud (
  osaamistasonkriteeri_id BIGINT NOT NULL,
  tekstipalanen_id BIGINT NOT NULL,
  kriteerit_order INTEGER NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (rev, osaamistasonkriteeri_id, tekstipalanen_id, kriteerit_order)
);

CREATE TABLE arvioinninkohde (
  id BIGINT PRIMARY KEY NOT NULL,
  arviointiasteikko_id BIGINT REFERENCES arviointiasteikko (id),
  otsikko_id BIGINT REFERENCES lokalisoitu_teksti (id),
  selite_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE arvioinninkohde_aud (
  id BIGINT NOT NULL,
  arviointiasteikko_id BIGINT,
  otsikko_id BIGINT,
  selite_id BIGINT REFERENCES lokalisoitu_teksti (id),
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (id, rev)
);

CREATE TABLE arvioinninkohde_osaamistasonkriteeri (
  arvioinninkohde_id BIGINT NOT NULL REFERENCES arvioinninkohde (id),
  osaamistason_kriteerit_id BIGINT NOT NULL REFERENCES osaamistasonkriteeri (id),
  PRIMARY KEY (arvioinninkohde_id, osaamistason_kriteerit_id),
  UNIQUE (osaamistason_kriteerit_id)
);

CREATE TABLE arvioinninkohde_osaamistasonkriteeri_aud (
  arvioinninkohde_id BIGINT NOT NULL,
  osaamistason_kriteerit_id BIGINT NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (rev, arvioinninkohde_id, osaamistason_kriteerit_id)
);

CREATE TABLE arvioinninkohdealue_arvioinninkohde (
  arvioinninkohdealue_id BIGINT NOT NULL REFERENCES arvioinninkohdealue (id),
  arvioinninkohde_id BIGINT NOT NULL REFERENCES arvioinninkohde (id),
  arvioinnin_kohteet_order INTEGER NOT NULL,
  PRIMARY KEY (arvioinninkohdealue_id, arvioinnin_kohteet_order),
  UNIQUE (arvioinninkohde_id)
);

CREATE TABLE arvioinninkohdealue_arvioinninkohde_aud (
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinninkohde_id BIGINT NOT NULL,
  arvioinnin_kohteet_order INTEGER NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (arvioinninkohdealue_id, arvioinninkohde_id, arvioinnin_kohteet_order, rev)
);

CREATE TABLE arvioinninkohdealue_aud (
  id BIGINT NOT NULL,
  otsikko_id BIGINT,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (id, rev)
);

-- Ammattitaitovaatimukset

CREATE TABLE ammattitaitovaatimuksenkohdealue (
    id BIGINT PRIMARY KEY NOT NULL,
    otsikko_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_aud (
    id BIGINT NOT NULL,
    otsikko_id BIGINT,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo (rev),
    PRIMARY KEY (id, rev)
);

CREATE TABLE ammattitaitovaatimuksenkohde (
    id BIGINT PRIMARY KEY NOT NULL,
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL REFERENCES ammattitaitovaatimuksenkohdealue (id),
    otsikko_id BIGINT REFERENCES lokalisoitu_teksti (id),
    selite_id BIGINT REFERENCES lokalisoitu_teksti (id)
);

CREATE TABLE ammattitaitovaatimuksenkohde_aud (
    id BIGINT NOT NULL,
    ammattitaitovaatimuksenkohdealue_id BIGINT,
    otsikko_id BIGINT,
    selite_id BIGINT,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo (rev),
    PRIMARY KEY (id, rev)
);

CREATE TABLE osaamistavoite (
    id BIGINT PRIMARY KEY NOT NULL,
    laajuus NUMERIC(10,2),
    pakollinen BOOLEAN NOT NULL,
    arviointi_id BIGINT REFERENCES arviointi (id),
    nimi_id BIGINT REFERENCES lokalisoitu_teksti (id),
    tavoitteet_id BIGINT REFERENCES lokalisoitu_teksti (id),
    tunnustaminen_id BIGINT REFERENCES lokalisoitu_teksti (id),
    esitieto_id BIGINT REFERENCES osaamistavoite (id),
    kieli CHARACTER VARYING(255),
    koodi_uri CHARACTER VARYING(255),
    koodi_arvo CHARACTER VARYING(255)
);

CREATE TABLE osaamistavoite_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo (rev),
    laajuus NUMERIC(10,2),
    pakollinen BOOLEAN,
    arviointi_id BIGINT,
    nimi_id BIGINT,
    tavoitteet_id BIGINT,
    tunnustaminen_id BIGINT,
    esitieto_id BIGINT,
    kieli CHARACTER VARYING(255),
    koodi_uri CHARACTER VARYING(255),
    koodi_arvo CHARACTER VARYING(255)
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_osaamistavoite (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL REFERENCES ammattitaitovaatimuksenkohdealue (id),
    osaamistavoite_id BIGINT NOT NULL REFERENCES osaamistavoite (id),
    jarjestys INTEGER NOT NULL,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys)
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_osaamistavoite_aud (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    osaamistavoite_id BIGINT,
    jarjestys INTEGER NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo (rev),
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys, rev)
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_omatutkinnonosa (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL REFERENCES ammattitaitovaatimuksenkohdealue (id),
    omatutkinnonosa_id BIGINT NOT NULL REFERENCES omatutkinnonosa (id),
    jarjestys INTEGER NOT NULL,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys)
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_omatutkinnonosa_aud (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    omatutkinnonosa_id BIGINT,
    jarjestys INTEGER NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER REFERENCES revinfo (rev),
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys, rev)
);

CREATE TABLE ammattitaitovaatimus (
    id BIGINT PRIMARY KEY NOT NULL,
    ammattitaitovaatimuksenkohde_id BIGINT NOT NULL REFERENCES ammattitaitovaatimuksenkohde (id),
    selite_id BIGINT REFERENCES lokalisoitu_teksti (id),
    koodi CHARACTER VARYING(20),
    jarjestys INTEGER
);

CREATE TABLE ammattitaitovaatimus_aud (
    id BIGINT NOT NULL,
    ammattitaitovaatimuksenkohde_id BIGINT,
    selite_id BIGINT,
    koodi CHARACTER VARYING(20),
    jarjestys INTEGER,
    rev INTEGER NOT NULL REFERENCES revinfo (rev),
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (id, rev)
);

-- Tutkinnonosa

CREATE TABLE tutkinnonosa_osaalue (
  id BIGINT PRIMARY KEY NOT NULL,
  nimi_id BIGINT REFERENCES lokalisoitu_teksti (id),
  kieli CHARACTER VARYING(255),
  koodi_uri CHARACTER VARYING(255),
  koodi_arvo CHARACTER VARYING(255),
  kuvaus_id BIGINT REFERENCES lokalisoitu_teksti (id),
  valmatelmasisalto_id BIGINT
);

CREATE TABLE tutkinnonosa_osaalue_aud (
  id BIGINT NOT NULL,
  nimi_id BIGINT,
  kieli CHARACTER VARYING(255),
  koodi_uri CHARACTER VARYING(255),
  koodi_arvo CHARACTER VARYING(255),
  kuvaus_id BIGINT REFERENCES lokalisoitu_teksti (id),
  valmatelmasisalto_id BIGINT,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (id, rev)
);

CREATE TABLE tutkinnonosa_osaalue_osaamistavoite (
  tutkinnonosa_osaalue_id BIGINT NOT NULL REFERENCES tutkinnonosa_osaalue (id),
  osaamistavoite_id BIGINT REFERENCES osaamistavoite (id),
  osaamistavoitteet_order INTEGER NOT NULL,
  PRIMARY KEY (tutkinnonosa_osaalue_id, osaamistavoitteet_order)
);

CREATE TABLE tutkinnonosa_osaalue_osaamistavoite_aud (
  tutkinnonosa_osaalue_id BIGINT NOT NULL,
  osaamistavoite_id BIGINT NOT NULL,
  osaamistavoitteet_order INTEGER NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (tutkinnonosa_osaalue_id, osaamistavoite_id, osaamistavoitteet_order, rev)
);

CREATE TABLE tutkinnonosa_tutkinnonosa_osaalue (
  tutkinnonosa_id BIGINT NOT NULL REFERENCES tutkinnonosa (id),
  tutkinnonosa_osaalue_id BIGINT REFERENCES tutkinnonosa_osaalue (id),
  osaalueet_order INTEGER NOT NULL,
  PRIMARY KEY (tutkinnonosa_id, osaalueet_order)
);

CREATE TABLE tutkinnonosa_tutkinnonosa_osaalue_aud (
  tutkinnonosa_id BIGINT NOT NULL,
  tutkinnonosa_osaalue_id BIGINT NOT NULL,
  osaalueet_order INTEGER NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo (rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo (rev),
  PRIMARY KEY (tutkinnonosa_id, tutkinnonosa_osaalue_id, osaalueet_order, rev)
);

-- Oma tutkinnonosa

ALTER TABLE omatutkinnonosa ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN arviointi_id BIGINT;


ALTER TABLE omatutkinnonosa ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT arviointi_id FOREIGN KEY (arviointi_id) REFERENCES arviointi (id);

ALTER TABLE omatutkinnonosa_aud ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN arviointi_id BIGINT;

ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti (id);
