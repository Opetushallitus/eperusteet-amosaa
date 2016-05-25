DROP TABLE opetussuunnitelma_liite;
DROP TABLE opetussuunnitelma_liite_aud;

ALTER TABLE koulutustoimija_aud DROP CONSTRAINT koulutustoimija_aud_organisaatio_key;
ALTER TABLE koulutustoimija_aud DROP CONSTRAINT koulutustoimija_aud_pkey;
ALTER TABLE koulutustoimija_aud ADD PRIMARY KEY (id, rev);

CREATE TABLE koulutustoimija_liite (
  koulutustoimija_id BIGINT NOT NULL REFERENCES koulutustoimija(id),
  liite_id UUID NOT NULL REFERENCES liite(id),
  PRIMARY KEY (koulutustoimija_id, liite_id)
);

CREATE TABLE koulutustoimija_liite_aud (
  koulutustoimija_id BIGINT NOT NULL,
  liite_id uuid NOT NULL,
  rev INTEGER NOT NULL REFERENCES revinfo(rev),
  revtype SMALLINT,
  revend INTEGER REFERENCES revinfo(rev),
  PRIMARY KEY (rev, koulutustoimija_id, liite_id)
);
