ALTER TABLE opintokokonaisuus ADD COLUMN tavoitteidenKuvaus_id int8;

ALTER TABLE opintokokonaisuus_aud ADD COLUMN tavoitteidenKuvaus_id int8;

alter table opintokokonaisuus
    add constraint FK5xcm37pnawqgvk85pgrvfq9ox
    foreign key (tavoitteidenKuvaus_id)
    references lokalisoituteksti;
