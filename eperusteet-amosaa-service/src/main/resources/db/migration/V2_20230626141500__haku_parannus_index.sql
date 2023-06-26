drop INDEX if exists sisaltoviite_tyyppi_index;
drop INDEX if exists sisaltoviite_owner_index;
drop INDEX if exists opetussuunnitelma_id_index;
drop INDEX if exists opetussuunnitelma_nimi_id_index;
drop INDEX if exists tutkinnonosa_omatutkinnonosa_id_index;
drop INDEX if exists sisaltoviite_omaosaalue_sisaltoviite_id_index;

CREATE INDEX sisaltoviite_tyyppi_index ON sisaltoviite(tyyppi);
CREATE INDEX sisaltoviite_owner_index ON sisaltoviite(owner_id);
CREATE INDEX opetussuunnitelma_id_index ON opetussuunnitelma(id);
CREATE INDEX opetussuunnitelma_nimi_id_index ON opetussuunnitelma(nimi_id);
CREATE INDEX tutkinnonosa_omatutkinnonosa_id_index ON tutkinnonosa(omatutkinnonosa_id);
CREATE INDEX sisaltoviite_omaosaalue_sisaltoviite_id_index ON sisaltoviite_omaosaalue(sisaltoviite_id);