create table ohje_koulutustoimija (
    ohje_id int8 not null,
    koulutustoimija_id int8 not null,
    primary key (ohje_id, koulutustoimija_id)
);

alter table ohje_koulutustoimija
    add constraint FKkpe9ythqh6vysukqtqq7ga7ie
    foreign key (koulutustoimija_id)
    references koulutustoimija;

alter table ohje_koulutustoimija
    add constraint FKqclpdpw2mfl538jfyph4c611j
    foreign key (ohje_id)
    references Ohje;
