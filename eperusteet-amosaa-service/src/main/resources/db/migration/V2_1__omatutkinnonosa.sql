-- Oma tutkinnonosa

ALTER TABLE omatutkinnonosa ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN ammattitaitovaatimukset_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa ADD COLUMN kuvaus_id BIGINT;

ALTER TABLE omatutkinnonosa ADD COLUMN koodi_uri VARCHAR(255);
ALTER TABLE omatutkinnonosa ADD COLUMN koodi_arvo VARCHAR(255);

ALTER TABLE omatutkinnonosa ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT ammattitaitovaatimukset_id_fkey FOREIGN KEY (ammattitaitovaatimukset_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa ADD CONSTRAINT kuvaus_id_fkey FOREIGN KEY (kuvaus_id) REFERENCES lokalisoitu_teksti(id);

-- Audit table

ALTER TABLE omatutkinnonosa_aud ADD COLUMN tavoitteet_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN ammattitaitovaatimukset_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN ammattitaidon_osoittamistavat_id BIGINT;
ALTER TABLE omatutkinnonosa_aud ADD COLUMN kuvaus_id BIGINT;

ALTER TABLE omatutkinnonosa_aud ADD COLUMN koodi_uri VARCHAR(255);
ALTER TABLE omatutkinnonosa_aud ADD COLUMN koodi_arvo VARCHAR(255);

ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT tavoitteet_id_fkey FOREIGN KEY (tavoitteet_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT ammattitaitovaatimukset_id_fkey FOREIGN KEY (ammattitaitovaatimukset_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT ammattitaidon_osoittamistavat_id_fkey FOREIGN KEY (ammattitaidon_osoittamistavat_id) REFERENCES lokalisoitu_teksti(id);
ALTER TABLE omatutkinnonosa_aud ADD CONSTRAINT kuvaus_id_fkey FOREIGN KEY (kuvaus_id) REFERENCES lokalisoitu_teksti(id);
