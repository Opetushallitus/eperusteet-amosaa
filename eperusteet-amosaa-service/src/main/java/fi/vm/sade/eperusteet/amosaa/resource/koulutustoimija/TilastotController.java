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

package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.tilastot.TilastotDto;
import fi.vm.sade.eperusteet.amosaa.dto.tilastot.ToimijaTilastotDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.tilastot.TilastotService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/tilastot")
@Api(value = "tilastot")
@InternalApi
public class TilastotController {

    @Autowired
    TilastotService service;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.GET)
    TilastotDto get() {
        return service.getTilastot();
    }

    @RequestMapping(value = "/toimijat", method = RequestMethod.GET)
    ToimijaTilastotDto getTilastotToimijakohtaisesti() {
        return service.getTilastotToimijakohtaisesti();
    }

    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot() {
        return opetussuunnitelmaService.getOpetussuunnitelmaTilastot();
    }

    @RequestMapping(value = "/opetussuunnitelmat/{sivu}/{sivukoko}", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastotSivu(@PathVariable("sivu") Integer sivu, @PathVariable("sivukoko") Integer sivukoko) {
        return opetussuunnitelmaService.getOpetussuunnitelmaTilastot(sivu, sivukoko);
    }
}
