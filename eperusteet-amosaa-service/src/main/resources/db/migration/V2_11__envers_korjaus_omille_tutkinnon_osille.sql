create table ammattitaitovaatimuksenkohde_ammattitaitovaatimus (                 
    ammattitaitovaatimuksenkohde_id int8 not null,                               
    vaatimukset_id int8 not null,                                                
    jarjestys int4 not null,                                                     
    primary key (ammattitaitovaatimuksenkohde_id, jarjestys)                     
);                                                                               
                                                                                 
create table ammattitaitovaatimuksenkohde_ammattitaitovaatimus_AUD (             
    REV int4 not null,                                                           
    ammattitaitovaatimuksenkohde_id int8 not null,                               
    vaatimukset_id int8 not null,                                                
    jarjestys int4 not null,                                                     
    REVTYPE int2,                                                                
    REVEND int4,                                                                 
    primary key (REV, ammattitaitovaatimuksenkohde_id, vaatimukset_id, jarjestys)
);                                                                               
