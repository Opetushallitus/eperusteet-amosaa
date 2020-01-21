alter table sisaltoviite add column peruste_id BIGINT REFERENCES peruste_cache(id);
alter table sisaltoviite_aud add column peruste_id BIGINT;