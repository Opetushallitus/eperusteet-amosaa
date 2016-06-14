ALTER TABLE omatutkinnonosa DROP COLUMN koodi;
ALTER TABLE omatutkinnonosa_aud DROP COLUMN koodi;

ALTER TABLE omatutkinnonosa ADD COLUMN koodi VARCHAR(255);
ALTER TABLE omatutkinnonosa_aud ADD COLUMN koodi VARCHAR(255);

ALTER TABLE omatutkinnonosa ADD COLUMN koodi_prefix VARCHAR(255);
ALTER TABLE omatutkinnonosa_aud ADD COLUMN koodi_prefix VARCHAR(255);