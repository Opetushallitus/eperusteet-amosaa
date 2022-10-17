ALTER TABLE opintokokonaisuus ADD COLUMN laajuusYksikko varchar(255);
ALTER TABLE opintokokonaisuus_aud ADD COLUMN laajuusYksikko varchar(255);
UPDATE opintokokonaisuus SET laajuusYksikko = 'OPINTOPISTE';
UPDATE opintokokonaisuus_aud SET laajuusYksikko = 'OPINTOPISTE';
