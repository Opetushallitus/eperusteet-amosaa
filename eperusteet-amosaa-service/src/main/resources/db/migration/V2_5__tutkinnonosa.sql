CREATE TABLE tutkinnonosa_osaalue (
  id BIGINT PRIMARY KEY NOT NULL,
  nimi_id BIGINT,
  kieli CHARACTER VARYING(255),
  koodi_uri CHARACTER VARYING(255),
  koodi_arvo CHARACTER VARYING(255),
  kuvaus_id BIGINT,
  valmatelmasisalto_id BIGINT,
  FOREIGN KEY (nimi_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (kuvaus_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE tutkinnonosa_osaalue_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  nimi_id BIGINT,
  kieli CHARACTER VARYING(255),
  koodi_uri CHARACTER VARYING(255),
  koodi_arvo CHARACTER VARYING(255),
  kuvaus_id BIGINT,
  valmatelmasisalto_id BIGINT,
  PRIMARY KEY (id, rev),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (kuvaus_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.tutkinnonosa_osaalue_osaamistavoite (
  tutkinnonosa_osaalue_id BIGINT NOT NULL,
  osaamistavoite_id BIGINT,
  osaamistavoitteet_order INTEGER NOT NULL,
  PRIMARY KEY (tutkinnonosa_osaalue_id, osaamistavoitteet_order),
  FOREIGN KEY (tutkinnonosa_osaalue_id) REFERENCES public.tutkinnonosa_osaalue (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (osaamistavoite_id) REFERENCES public.osaamistavoite (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.tutkinnonosa_osaalue_osaamistavoite_aud (
  rev INTEGER NOT NULL,
  tutkinnonosa_osaalue_id BIGINT NOT NULL,
  osaamistavoite_id BIGINT NOT NULL,
  osaamistavoitteet_order INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, tutkinnonosa_osaalue_id, osaamistavoite_id, osaamistavoitteet_order),
  FOREIGN KEY (rev) REFERENCES public.revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES public.revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.tutkinnonosa_tutkinnonosa_osaalue (
  tutkinnonosa_id BIGINT NOT NULL,
  tutkinnonosa_osaalue_id BIGINT,
  osaalueet_order INTEGER NOT NULL,
  PRIMARY KEY (tutkinnonosa_id, osaalueet_order),
  FOREIGN KEY (tutkinnonosa_id) REFERENCES public.tutkinnonosa (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (tutkinnonosa_osaalue_id) REFERENCES public.tutkinnonosa_osaalue (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public.tutkinnonosa_tutkinnonosa_osaalue_aud (
  rev INTEGER NOT NULL,
  tutkinnonosa_id BIGINT NOT NULL,
  tutkinnonosa_osaalue_id BIGINT NOT NULL,
  osaalueet_order INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, tutkinnonosa_id, tutkinnonosa_osaalue_id, osaalueet_order),
  FOREIGN KEY (rev) REFERENCES public.revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES public.revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Oma tutkinnonosa

ALTER TABLE omatutkinnonosa ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN arviointi_id BIGINT;


ALTER TABLE omatutkinnonosa ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT arviointi_id FOREIGN KEY (arviointi_id) REFERENCES arviointi (id);

-- Audit oma tutkinnonosa

ALTER TABLE omatutkinnonosa_aud ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN arviointi_id BIGINT;

ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti (id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT arviointi_id FOREIGN KEY (arviointi_id) REFERENCES arviointi (id);
