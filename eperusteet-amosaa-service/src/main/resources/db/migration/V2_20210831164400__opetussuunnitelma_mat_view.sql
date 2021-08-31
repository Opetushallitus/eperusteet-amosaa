drop materialized view if exists julkaistu_opetussuunnitelma_data_view;

create materialized view julkaistu_opetussuunnitelma_data_view as
	SELECT
           data->>'id' as id,
           data->'nimi' as nimi,
           data->'koulutustoimija' as koulutustoimija,
           data->'peruste' as peruste,
           data->>'koulutustyyppi' as koulutustyyppi,
           data->>'oppilaitosTyyppiKoodiUri' as "oppilaitosTyyppiKoodiUri",
           data->>'tyyppi' as tyyppi
        FROM julkaisu j
        INNER JOIN julkaisu_data d on d.id = j.data_id
	    INNER JOIN opetussuunnitelma o on o.id = j.opetussuunnitelma_id
	    WHERE revision = (SELECT MAX(revision) FROM julkaisu j2 WHERE j.opetussuunnitelma_id = j2.opetussuunnitelma_id)
	    AND o.tila != 'POISTETTU';

