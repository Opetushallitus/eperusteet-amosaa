/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nkala
 */
@Service
public class KayttajaoikeusServiceImpl implements KayttajaoikeusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Override
    public List<KayttajaoikeusDto> getKayttooikeudet(KayttajaoikeusDto oikeusQuery) {

        List<Kayttajaoikeus> result = new ArrayList<>();
        Reference kayttaja = oikeusQuery.getKayttaja();
        Reference koulutustoimija = oikeusQuery.getKoulutustoimija();

        if (kayttaja != null && koulutustoimija != null) {
            result = kayttajaoikeusRepository.findAllKayttajaoikeus(kayttaja.getIdLong(), koulutustoimija.getIdLong());
        }

        if (kayttaja != null) {
        }

        if (koulutustoimija != null) {

        }

        return mapper.mapAsList(result, KayttajaoikeusDto.class);
    }

}
