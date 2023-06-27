drop INDEX if exists sisaltoviite_tosa_id_index;
drop INDEX if exists sisaltoviite_tekstikappale_id_index;

CREATE INDEX sisaltoviite_tosa_id_index ON sisaltoviite(tosa_id);
CREATE INDEX sisaltoviite_tekstikappale_id_index ON sisaltoviite(tekstikappale_id);

analyze;