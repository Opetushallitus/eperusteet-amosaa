update peruste_cache set koulutustyyppi = 'PERUSTUTKINTO' where (peruste::json)->>'koulutustyyppi' = 'koulutustyyppi_1' AND koulutustyyppi is null
