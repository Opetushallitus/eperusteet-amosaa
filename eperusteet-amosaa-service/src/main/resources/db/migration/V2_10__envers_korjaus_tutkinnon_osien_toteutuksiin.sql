ALTER TABLE tutkinnonosa_toteutus DROP COLUMN jnro;
ALTER TABLE tutkinnonosa_toteutus_aud DROP COLUMN jnro;

create table tutkinnonosa_tutkinnonosa_toteutus (           
    tutkinnonosa_id int8 not null,                          
    toteutukset_id int8 not null,                           
    jnro int4 not null,                                     
    primary key (tutkinnonosa_id, jnro)                     
);                                                          
                                                            
create table tutkinnonosa_tutkinnonosa_toteutus_AUD (       
    REV int4 not null,                                      
    tutkinnonosa_id int8 not null,                          
    toteutukset_id int8 not null,                           
    jnro int4 not null,                                     
    REVTYPE int2,                                           
    REVEND int4,                                            
    primary key (REV, tutkinnonosa_id, toteutukset_id, jnro)
);                                                          