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

package fi.vm.sade.eperusteet.amosaa.service.tilastot.impl;

import fi.vm.sade.eperusteet.amosaa.dto.tilastot.TilastotDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.tilastot.TilastotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nkala
 */
@Service
public class TilastotServiceImpl implements TilastotService {

    @Autowired
    KayttajaRepository kayttajat;
    
    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmat;

    @Autowired
    KoulutustoimijaRepository koulutustoimijat;

    @Override
    public TilastotDto getTilastot() {
        TilastotDto result = new TilastotDto();
        result.setKayttajia(kayttajat.count());
        result.setKoulutuksenjarjestajia(koulutustoimijat.count());
        result.setOpetussuunnitelmia(opetussuunnitelmat.count());
        return result;
    }

}
