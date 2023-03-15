ALTER TABLE omaosaaluetoteutus ADD COLUMN oletustoteutus boolean DEFAULT false;
ALTER TABLE omaosaaluetoteutus_aud ADD COLUMN oletustoteutus boolean;

alter table omaosaaluetoteutus drop constraint FK_k7ptod6eh40of5kf5fcft5nn7;
alter table omaosaaluetoteutus drop constraint FK_jl7o5akd4adh3fynwjui9ryqi;

update omaosaaluetoteutus set arvioinnista_id = null;
update omaosaaluetoteutus set tavatjaymparisto_id = null;

alter table omaosaaluetoteutus
    add constraint FK_k7ptod6eh40of5kf5fcft5nn2
    foreign key (arvioinnista_id)
    references tekstiosa;

alter table omaosaaluetoteutus
    add constraint FK_jl7o5akd4adh3fynwjui9ryq2
    foreign key (tavatjaymparisto_id)
    references tekstiosa;