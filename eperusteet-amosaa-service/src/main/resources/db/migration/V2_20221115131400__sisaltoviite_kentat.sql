ALTER TABLE sisaltoviite ADD COLUMN luoja VARCHAR(255);
ALTER TABLE sisaltoviite ADD COLUMN luotu TIMESTAMP;
ALTER TABLE sisaltoviite ADD COLUMN muokattu TIMESTAMP;
ALTER TABLE sisaltoviite ADD COLUMN muokkaaja VARCHAR(255);

ALTER TABLE sisaltoviite_aud ADD COLUMN luoja VARCHAR(255);
ALTER TABLE sisaltoviite_aud ADD COLUMN luotu TIMESTAMP;
ALTER TABLE sisaltoviite_aud ADD COLUMN muokattu TIMESTAMP;
ALTER TABLE sisaltoviite_aud ADD COLUMN muokkaaja VARCHAR(255);