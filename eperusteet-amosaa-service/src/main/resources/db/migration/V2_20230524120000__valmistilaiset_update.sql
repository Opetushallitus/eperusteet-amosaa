ALTER TABLE opetussuunnitelma DISABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;
UPDATE opetussuunnitelma SET tila = 'LUONNOS' WHERE tila = 'VALMIS' AND tyyppi = 'YHTEINEN';
ALTER TABLE opetussuunnitelma ENABLE TRIGGER tg_refresh_opetussuunnitelma_data_view_after_tila_update;