UPDATE opetussuunnitelma_muokkaustieto
SET kohde_id = sv.id,
    kohde = LOWER(sv.tyyppi)
    FROM sisaltoviite sv
WHERE sv.tekstikappale_id = kohde_id;
