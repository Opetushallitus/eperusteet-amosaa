ALTER TABLE sisaltoviite_omaosaalue ADD COLUMN osaAlueet_ORDER int4;
ALTER TABLE sisaltoviite_omaosaalue_aud ADD COLUMN osaAlueet_ORDER int4;

update sisaltoviite_omaosaalue
set osaAlueet_ORDER = subquery.jarjestys
from (select sisaltoviite_id,
             osaalueet_id,
             row_number() over (partition by sisaltoviite_id order by osaalueet_id) - 1 as jarjestys
      from sisaltoviite_omaosaalue) as subquery
where sisaltoviite_omaosaalue.sisaltoviite_id = subquery.sisaltoviite_id
  and sisaltoviite_omaosaalue.osaalueet_id = subquery.osaalueet_id;

update sisaltoviite_omaosaalue_aud
set osaAlueet_ORDER = subquery.jarjestys
from (select rev, sisaltoviite_id,
             osaalueet_id,
             row_number() over (partition by rev, sisaltoviite_id order by osaalueet_id) -1 as jarjestys
      from sisaltoviite_omaosaalue_aud) as subquery
where sisaltoviite_omaosaalue_aud.rev = subquery.rev
  and sisaltoviite_omaosaalue_aud.sisaltoviite_id = subquery.sisaltoviite_id
  and sisaltoviite_omaosaalue_aud.osaalueet_id = subquery.osaalueet_id;

alter table sisaltoviite_omaosaalue drop constraint IF EXISTS sisaltoviite_omaosaalue_pkey;
ALTER TABLE sisaltoviite_omaosaalue ADD PRIMARY KEY (sisaltoviite_id, osaAlueet_ORDER);
alter table sisaltoviite_omaosaalue_aud drop constraint IF EXISTS sisaltoviite_omaosaalue_aud_pkey;
ALTER TABLE sisaltoviite_omaosaalue_aud ADD PRIMARY KEY (REV, sisaltoviite_id, osaalueet_id, osaAlueet_ORDER);

ALTER TABLE sisaltoviite_omaosaalue ALTER COLUMN osaAlueet_ORDER SET NOT NULL;
ALTER TABLE sisaltoviite_omaosaalue_aud ALTER COLUMN osaAlueet_ORDER SET NOT NULL;

alter table sisaltoviite_omaosaalue drop constraint uk_ihwf5f3ske0jbd6egp00lcryy;

alter table sisaltoviite_omaosaalue
    add constraint FK_jmd85ls81qb3wei663e3tyccc
    foreign key (osaalueet_id)
    references omaosaalue;

alter table sisaltoviite_omaosaalue
    add constraint FK_no1r3baim3qb54seraxfoilfl
    foreign key (sisaltoviite_id)
    references sisaltoviite;