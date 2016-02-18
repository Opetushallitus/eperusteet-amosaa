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

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.resource.util.AbstractRevisionController;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.YhteisetService;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{kid}/yhteiset/{id}")
@Api(value = "yhteiset")
public class YhteisetController implements AbstractRevisionController {

    @Autowired
    private YhteisetService service;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public YhteisetDto getYhteiset(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id) {
        return service.getYhteiset(kid, id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public YhteisetDto updateYhteiset(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id,
            @RequestBody(required = false) YhteisetDto body) {
        return service.updateYhteiset(kid, id, body);
    }

    @Override
    public RevisionService getService() {
        return service;
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    @ResponseBody
    public List<PoistettuDto> getYhteisetPoistetut(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id) {
        return service.getYhteisetPoistetut(kid, id);
    }

    @RequestMapping(value = "/sisalto", method = RequestMethod.GET)
    @ResponseBody
    public YhteisetSisaltoDto getYhteisetSisalto(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id) {
        return service.getYhteisetSisalto(kid, id);
    }

}
