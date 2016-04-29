CREATE TABLE arviointi (
  id BIGINT PRIMARY KEY NOT NULL,
  lisatiedot_id BIGINT,
  FOREIGN KEY (lisatiedot_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arviointi_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  lisatiedot_id BIGINT,
  PRIMARY KEY (id, rev),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arvioinninkohdealue (
  id BIGINT PRIMARY KEY NOT NULL,
  otsikko_id BIGINT,
  FOREIGN KEY (otsikko_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arviointi_arvioinninkohdealue (
  arviointi_id BIGINT NOT NULL,
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinnin_kohdealueet_order INTEGER NOT NULL,
  PRIMARY KEY (arviointi_id, arvioinnin_kohdealueet_order),
  FOREIGN KEY (arvioinninkohdealue_id) REFERENCES arvioinninkohdealue (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (arviointi_id) REFERENCES arviointi (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX uk_arviointi_arvioinninkohdealue ON arviointi_arvioinninkohdealue USING BTREE (arvioinninkohdealue_id);

CREATE TABLE arviointi_arvioinninkohdealue_aud (
  rev INTEGER NOT NULL,
  arviointi_id BIGINT NOT NULL,
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinnin_kohdealueet_order INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, arviointi_id, arvioinninkohdealue_id, arvioinnin_kohdealueet_order),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arviointiasteikko (
  id BIGINT PRIMARY KEY NOT NULL
);

CREATE TABLE osaamistaso (
  id BIGINT PRIMARY KEY NOT NULL,
  otsikko_id BIGINT,
  FOREIGN KEY (otsikko_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arviointiasteikko_osaamistaso (
  arviointiasteikko_id BIGINT NOT NULL,
  osaamistasot_id BIGINT NOT NULL,
  osaamistasot_order INTEGER NOT NULL,
  PRIMARY KEY (arviointiasteikko_id, osaamistasot_order),
  FOREIGN KEY (arviointiasteikko_id) REFERENCES arviointiasteikko (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (osaamistasot_id) REFERENCES osaamistaso (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX uk_arviointiasteikko_osaamistaso ON arviointiasteikko_osaamistaso USING BTREE (osaamistasot_id);

CREATE TABLE osaamistasonkriteeri (
  id BIGINT PRIMARY KEY NOT NULL,
  osaamistaso_id BIGINT,
  FOREIGN KEY (osaamistaso_id) REFERENCES osaamistaso (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE osaamistasonkriteeri_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  osaamistaso_id BIGINT,
  PRIMARY KEY (id, rev),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE osaamistasonkriteeri_tekstipalanen (
  osaamistasonkriteeri_id BIGINT NOT NULL,
  tekstipalanen_id BIGINT NOT NULL,
  kriteerit_order INTEGER NOT NULL,
  PRIMARY KEY (osaamistasonkriteeri_id, kriteerit_order),
  FOREIGN KEY (osaamistasonkriteeri_id) REFERENCES osaamistasonkriteeri (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (tekstipalanen_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE osaamistasonkriteeri_tekstipalanen_aud (
  rev INTEGER NOT NULL,
  osaamistasonkriteeri_id BIGINT NOT NULL,
  tekstipalanen_id BIGINT NOT NULL,
  kriteerit_order INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, osaamistasonkriteeri_id, tekstipalanen_id, kriteerit_order),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arvioinninkohde (
  id BIGINT PRIMARY KEY NOT NULL,
  arviointiasteikko_id BIGINT,
  otsikko_id BIGINT,
  selite_id BIGINT,
  FOREIGN KEY (selite_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (arviointiasteikko_id) REFERENCES arviointiasteikko (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (otsikko_id) REFERENCES lokalisoitu_teksti(id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arvioinninkohde_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  arviointiasteikko_id BIGINT,
  otsikko_id BIGINT,
  selite_id BIGINT,
  PRIMARY KEY (id, rev),
  FOREIGN KEY (selite_id) REFERENCES lokalisoitu_teksti (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arvioinninkohde_osaamistasonkriteeri (
  arvioinninkohde_id BIGINT NOT NULL,
  osaamistason_kriteerit_id BIGINT NOT NULL,
  PRIMARY KEY (arvioinninkohde_id, osaamistason_kriteerit_id),
  FOREIGN KEY (arvioinninkohde_id) REFERENCES arvioinninkohde (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (osaamistason_kriteerit_id) REFERENCES osaamistasonkriteeri (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX uk_arvioinninkohde_osaamistasonkriteeri ON arvioinninkohde_osaamistasonkriteeri USING BTREE (osaamistason_kriteerit_id);

CREATE TABLE arvioinninkohde_osaamistasonkriteeri_aud (
  rev INTEGER NOT NULL,
  arvioinninkohde_id BIGINT NOT NULL,
  osaamistason_kriteerit_id BIGINT NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, arvioinninkohde_id, osaamistason_kriteerit_id),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE arvioinninkohdealue_arvioinninkohde (
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinninkohde_id BIGINT NOT NULL,
  arvioinnin_kohteet_order INTEGER NOT NULL,
  PRIMARY KEY (arvioinninkohdealue_id, arvioinnin_kohteet_order),
  FOREIGN KEY (arvioinninkohde_id) REFERENCES arvioinninkohde (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (arvioinninkohdealue_id) REFERENCES arvioinninkohdealue (id)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE UNIQUE INDEX uk_arvioinninkohdealue_arvioinninkohde ON arvioinninkohdealue_arvioinninkohde USING BTREE (arvioinninkohde_id);

CREATE TABLE arvioinninkohdealue_arvioinninkohde_aud (
  rev INTEGER NOT NULL,
  arvioinninkohdealue_id BIGINT NOT NULL,
  arvioinninkohde_id BIGINT NOT NULL,
  arvioinnin_kohteet_order INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  PRIMARY KEY (rev, arvioinninkohdealue_id, arvioinninkohde_id, arvioinnin_kohteet_order),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE arvioinninkohdealue_aud (
  id BIGINT NOT NULL,
  rev INTEGER NOT NULL,
  revtype SMALLINT,
  revend INTEGER,
  otsikko_id BIGINT,
  PRIMARY KEY (id, rev),
  FOREIGN KEY (rev) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (revend) REFERENCES revinfo (rev)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

