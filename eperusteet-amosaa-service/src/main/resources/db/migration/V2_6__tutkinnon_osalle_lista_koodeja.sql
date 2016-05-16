ALTER TABLE tutkinnonosa_toteutus DROP COLUMN osaamisalaKoodi;
ALTER TABLE tutkinnonosa_toteutus DROP COLUMN oppiaineKoodi;
ALTER TABLE tutkinnonosa_toteutus DROP COLUMN kurssiKoodi;

ALTER TABLE tutkinnonosa_toteutus_aud DROP COLUMN osaamisalaKoodi;
ALTER TABLE tutkinnonosa_toteutus_aud DROP COLUMN oppiaineKoodi;
ALTER TABLE tutkinnonosa_toteutus_aud DROP COLUMN kurssiKoodi;

create table TutkinnonosaToteutus_koodit (            
    TutkinnonosaToteutus_id int8 not null,            
    koodit varchar(255)                               
);                                                    
                                                      
create table TutkinnonosaToteutus_koodit_AUD (        
    REV int4 not null,                                
    TutkinnonosaToteutus_id int8 not null,            
    koodit varchar(255) not null,                     
    REVTYPE int2,                                     
    REVEND int4,                                      
    primary key (REV, TutkinnonosaToteutus_id, koodit)
);                                                    
