ALTER TABLE koulutustoimija ADD COLUMN organisaatioRyhma boolean NOT NULL DEFAULT FALSE;

ALTER TABLE koulutustoimija_aud ADD COLUMN organisaatioRyhma boolean;

