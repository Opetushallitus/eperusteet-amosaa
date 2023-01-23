create table peruste_cache_koulutuskoodi (
    id int8 not null,
    koulutusalakoodi varchar(255),
    koulutuskoodiArvo varchar(255),
    koulutuskoodiUri varchar(255),
    opintoalakoodi varchar(255),
    cached_peruste_id int8,
    nimi_id int8,
    primary key (id)
);

alter table peruste_cache_koulutuskoodi
    add constraint FK_mtrgns6fooihwolr9ed129xyb
    foreign key (cached_peruste_id)
    references peruste_cache;

alter table peruste_cache_koulutuskoodi
    add constraint FK_gkc0hrqhrff334xposb6dhywf
    foreign key (nimi_id)
    references lokalisoituteksti;