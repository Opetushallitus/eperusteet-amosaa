ALTER TABLE opetussuunnitelma ADD COLUMN koulutustyyppi varchar(255);
ALTER TABLE opetussuunnitelma_AUD ADD COLUMN koulutustyyppi varchar(255);

ALTER TABLE sisaltoviite add column pohjanTekstikappale_id int8;
ALTER TABLE sisaltoviite add column nayta_pohjan_teksti boolean DEFAULT true;

ALTER TABLE sisaltoviite_aud add column pohjanTekstikappale_id int8;
ALTER TABLE sisaltoviite_aud add column nayta_pohjan_teksti boolean;

alter table sisaltoviite
    add constraint FK_qvnk9itnidnwjwfsjchtvqpev
    foreign key (pohjanTekstikappale_id)
    references tekstikappale;
