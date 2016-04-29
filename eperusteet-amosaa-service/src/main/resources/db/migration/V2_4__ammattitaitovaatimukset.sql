CREATE TABLE ammattitaitovaatimuksenkohdealue (
    id BIGINT PRIMARY KEY NOT NULL,
    otsikko_id BIGINT,
    FOREIGN KEY (otsikko_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_aud (
    id BIGINT NOT NULL,
    otsikko_id BIGINT,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohde (
    id BIGINT PRIMARY KEY NOT NULL,
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    otsikko_id BIGINT,
    selite_id BIGINT,
    FOREIGN KEY (ammattitaitovaatimuksenkohdealue_id) REFERENCES ammattitaitovaatimuksenkohdealue (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (otsikko_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (selite_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohde_aud (
    id BIGINT NOT NULL,
    ammattitaitovaatimuksenkohdealue_id BIGINT,
    otsikko_id BIGINT,
    selite_id BIGINT,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE osaamistavoite (
    id BIGINT PRIMARY KEY NOT NULL,
    laajuus NUMERIC(10,2),
    pakollinen BOOLEAN NOT NULL,
    arviointi_id BIGINT,
    nimi_id BIGINT,
    tavoitteet_id BIGINT,
    tunnustaminen_id BIGINT,
    esitieto_id BIGINT,
    kieli CHARACTER VARYING(255),
    koodi_uri CHARACTER VARYING(255),
    koodi_arvo CHARACTER VARYING(255),
    FOREIGN KEY (arviointi_id) REFERENCES arviointi (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (esitieto_id) REFERENCES osaamistavoite (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (nimi_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (tunnustaminen_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE osaamistavoite_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    laajuus NUMERIC(10,2),
    pakollinen BOOLEAN,
    arviointi_id BIGINT,
    nimi_id BIGINT,
    tavoitteet_id BIGINT,
    tunnustaminen_id BIGINT,
    esitieto_id BIGINT,
    kieli CHARACTER VARYING(255),
    koodi_uri CHARACTER VARYING(255),
    koodi_arvo CHARACTER VARYING(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_osaamistavoite (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    osaamistavoite_id BIGINT NOT NULL,
    jarjestys INTEGER NOT NULL,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys),
    FOREIGN KEY (ammattitaitovaatimuksenkohdealue_id) REFERENCES ammattitaitovaatimuksenkohdealue (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (osaamistavoite_id) REFERENCES osaamistavoite (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_osaamistavoite_aud (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    osaamistavoite_id BIGINT,
    jarjestys INTEGER NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_tutkinnonosa (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    tutkinnonosa_id BIGINT NOT NULL,
    jarjestys INTEGER NOT NULL,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys),
    FOREIGN KEY (ammattitaitovaatimuksenkohdealue_id) REFERENCES ammattitaitovaatimuksenkohdealue (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (tutkinnonosa_id) REFERENCES tutkinnonosa (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimuksenkohdealue_tutkinnonosa_aud (
    ammattitaitovaatimuksenkohdealue_id BIGINT NOT NULL,
    tutkinnonosa_id BIGINT,
    jarjestys INTEGER NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (ammattitaitovaatimuksenkohdealue_id, jarjestys, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimus (
    id BIGINT PRIMARY KEY NOT NULL,
    ammattitaitovaatimuksenkohde_id BIGINT NOT NULL,
    selite_id BIGINT,
    koodi CHARACTER VARYING(20),
    jarjestys INTEGER,
    FOREIGN KEY (ammattitaitovaatimuksenkohde_id) REFERENCES ammattitaitovaatimuksenkohde (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (selite_id) REFERENCES lokalisoitu_teksti (id)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ammattitaitovaatimus_aud (
    id BIGINT NOT NULL,
    ammattitaitovaatimuksenkohde_id BIGINT,
    selite_id BIGINT,
    koodi CHARACTER VARYING(20),
    jarjestys INTEGER,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    revend INTEGER,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    FOREIGN KEY (revend) REFERENCES revinfo (rev)
    MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

