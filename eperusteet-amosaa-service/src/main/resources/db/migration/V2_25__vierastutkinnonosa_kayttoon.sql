ALTER TABLE vierastutkinnonosa ADD COLUMN cperuste_id BIGINT NOT NULL REFERENCES peruste_cache(id);
ALTER TABLE vierastutkinnonosa ADD COLUMN tosa_id BIGINT NOT NULL;
ALTER TABLE vierastutkinnonosa ADD COLUMN peruste_id BIGINT NOT NULL;

ALTER TABLE vierastutkinnonosa_aud ADD COLUMN cperuste_id BIGINT;
ALTER TABLE vierastutkinnonosa_aud ADD COLUMN tosa_id BIGINT;
ALTER TABLE vierastutkinnonosa_aud ADD COLUMN peruste_id BIGINT;