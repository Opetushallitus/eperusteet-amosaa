ALTER TABLE opetussuunnitelma DISABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;
update opetussuunnitelma ops set tila = 'JULKAISTU' where exists (select 1 from julkaisu where opetussuunnitelma_id = ops.id) AND tila in ('LUONNOS', 'VALMIS');
ALTER TABLE opetussuunnitelma ENABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;
