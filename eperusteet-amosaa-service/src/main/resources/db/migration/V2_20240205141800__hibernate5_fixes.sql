ALTER TABLE koulutuksenjarjestaja_AUD ADD COLUMN IF NOT EXISTS koulutuksenJarjestajat_ORDER int4;
ALTER TABLE koulutuksenosan_laajaalainen_osaaminen_AUD ADD COLUMN IF NOT EXISTS laajaalaisetosaamiset_ORDER int4;
ALTER TABLE sisaltoviite_AUD ADD COLUMN IF NOT EXISTS lapset_ORDER int4;