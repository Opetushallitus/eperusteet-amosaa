ALTER TABLE opintokokonaisuus_tavoite ALTER COLUMN tavoite_koodi DROP NOT NULL;

ALTER TABLE opintokokonaisuus_tavoite ADD COLUMN tavoite_id int8;

ALTER TABLE opintokokonaisuus_tavoite_aud ADD COLUMN tavoite_id int8;

alter table opintokokonaisuus_tavoite
    add constraint FK_539nv0ubbfhj7g8j8c2g61w05
    foreign key (tavoite_id)
    references lokalisoituteksti;
