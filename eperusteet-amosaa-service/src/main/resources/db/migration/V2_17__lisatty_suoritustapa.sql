ALTER TABLE opetussuunnitelma ADD COLUMN suoritustapa VARCHAR(255) NOT NULL DEFAULT('ops');

ALTER TABLE opetussuunnitelma_aud ADD COLUMN suoritustapa VARCHAR(255);