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
package fi.vm.sade.eperusteet.amosaa.resource.util;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.service.external.ArviointiasteikkoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/api/arviointiasteikot")
@Api(value = "Arviointiasteikot")
@ApiIgnore
public class ArviointiasteikkoController {

    @Autowired
    ArviointiasteikkoService arviointiasteikkoService;


    @RequestMapping(method = RequestMethod.GET)
    public List<ArviointiasteikkoDto> getAllArviointiasteikot() {
        List<ArviointiasteikkoDto> all = arviointiasteikkoService.getAll();
        return all;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ArviointiasteikkoDto> getArviointiasteikko(@PathVariable final Long id) {
        return ResponseEntity.ok(arviointiasteikkoService.get(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public void updateArviointiasteikko() {
        arviointiasteikkoService.update();
    }
}
