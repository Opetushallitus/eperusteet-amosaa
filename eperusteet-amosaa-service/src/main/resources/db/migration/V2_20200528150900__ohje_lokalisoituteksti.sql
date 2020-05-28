alter table ohje add column lokalisoituKysymys_id int8;
alter table ohje add column lokalisoituVastaus_id int8;
alter table ohje add column luoja varchar(255);
alter table ohje add column luotu timestamp;
alter table ohje add column muokattu timestamp;
alter table ohje add column muokkaaja varchar(255);

alter table Ohje
    add constraint FKc5drgimm3gwxrdbvd47huvxhg
    foreign key (lokalisoituKysymys_id)
    references lokalisoituteksti;

alter table Ohje
    add constraint FKtcw9v8mai12hje5dok9i185ie
    foreign key (lokalisoituVastaus_id)
    references lokalisoituteksti;
