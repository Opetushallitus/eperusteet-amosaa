ALTER TABLE suorituspolku ADD COLUMN osasuorituspolku BOOLEAN DEFAULT FALSE;
ALTER TABLE suorituspolku_aud ADD COLUMN osasuorituspolku BOOLEAN DEFAULT FALSE;

ALTER TABLE suorituspolku ADD COLUMN osasuorituspolku_laajuus NUMERIC(10,2);
ALTER TABLE suorituspolku_aud ADD COLUMN osasuorituspolku_laajuus NUMERIC(10,2);