create table kayttaja_opetussuunnitelma (
    kayttaja_id int8 not null,
    suosikit_id int8 not null,
    primary key (kayttaja_id, suosikit_id)
);