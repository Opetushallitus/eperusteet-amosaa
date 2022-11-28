alter table omaosaalue
    add column osaamistavoitteet_id int8
    references ammattitaitovaatimukset2019;

alter table omaosaalue_aud
    add column osaamistavoitteet_id int8
    references ammattitaitovaatimukset2019;