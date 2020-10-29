ALTER TABLE opintokokonaisuus DROP COLUMN IF EXISTS tavoitteidenKuvaus_id;
ALTER TABLE opintokokonaisuus_aud DROP COLUMN IF EXISTS tavoitteidenKuvaus_id;
ALTER TABLE opintokokonaisuus DROP COLUMN IF EXISTS laajuus;
ALTER TABLE opintokokonaisuus_aud DROP COLUMN IF EXISTS laajuus;
ALTER TABLE opintokokonaisuus DROP COLUMN IF EXISTS tyyppi;
ALTER TABLE opintokokonaisuus_aud DROP COLUMN IF EXISTS tyyppi;
ALTER TABLE opintokokonaisuus DROP CONSTRAINT IF EXISTS FK5xcm37pnawqgvk85pgrvfq9ox;
ALTER TABLE ohje DROP COLUMN IF EXISTS toteutus;

ALTER TABLE opintokokonaisuus ADD COLUMN tavoitteidenKuvaus_id int8;
ALTER TABLE opintokokonaisuus_aud ADD COLUMN tavoitteidenKuvaus_id int8;

alter table opintokokonaisuus
    add constraint FK5xcm37pnawqgvk85pgrvfq9ox
    foreign key (tavoitteidenKuvaus_id)
    references lokalisoituteksti;

ALTER TABLE ohje ADD COLUMN toteutus varchar(255) NOT NULL DEFAULT 'ammatillinen';

ALTER TABLE opintokokonaisuus ADD COLUMN laajuus int4;
ALTER TABLE opintokokonaisuus_aud ADD COLUMN laajuus int4;

ALTER TABLE opintokokonaisuus ADD COLUMN tyyppi varchar(255);
ALTER TABLE opintokokonaisuus_aud ADD COLUMN tyyppi varchar(255);

alter table opintokokonaisuus_opintokokonaisuus_arviointi
    drop constraint UK_n0vggrkvgnbdn9kfiqf0ac8cl;

alter table opintokokonaisuus_opintokokonaisuus_tavoite
    drop constraint UK_2t48omwaofqnoyjdgig8sdu7e;
