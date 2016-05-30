ALTER TABLE tutkinnonosa ADD COLUMN osaamisenOsoittaminen_id BIGINT REFERENCES lokalisoituteksti(id);
ALTER TABLE tutkinnonosa_aud ADD COLUMN osaamisenOsoittaminen_id BIGINT;