DELETE FROM suorituspolku_rivi *;
DELETE FROM suorituspolku_rivi_aud *;

ALTER TABLE suorituspolku_rivi DROP COLUMN rakennemoduuli;
ALTER TABLE suorituspolku_rivi_aud DROP COLUMN rakennemoduuli;

ALTER TABLE suorituspolku_rivi ADD COLUMN rakennemoduuli uuid NOT NULL;
ALTER TABLE suorituspolku_rivi_aud ADD COLUMN rakennemoduuli uuid;

ALTER TABLE suorituspolku_rivi ADD CONSTRAINT suorituspolku_rivi_suorituspolku_rakennemoduuli_unique UNIQUE(suorituspolku_id, rakennemoduuli);