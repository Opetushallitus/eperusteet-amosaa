CREATE INDEX julkaisu_opetussuunnitelma_id_index ON julkaisu(opetussuunnitelma_id);
CREATE INDEX opetussuunnitelma_koulutustoimija_id_index on opetussuunnitelma(koulutustoimija_id);
CREATE INDEX opetussuunnitelma_peruste_cache_id_index on opetussuunnitelma(peruste_id);

analyze;