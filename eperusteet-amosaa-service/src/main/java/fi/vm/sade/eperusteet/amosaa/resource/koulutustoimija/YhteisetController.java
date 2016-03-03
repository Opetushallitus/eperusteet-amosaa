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

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.resource.util.AbstractRevisionController;
import fi.vm.sade.eperusteet.amosaa.resource.util.TekstiKappaleViiteAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.YhteisetService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;

import java.util.List;

import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{baseId}/yhteiset/{id}")
@Api(value = "yhteiset")
public class YhteisetController implements AbstractRevisionController, TekstiKappaleViiteAbstractController {

    @Autowired
    private YhteisetService yhteisetService;

    @Autowired
    private TekstiKappaleViiteService tkvService;

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    public ResponseEntity<YhteisetDto> getYhteiset(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        YhteisetDto yhteisetDto = yhteisetService.getYhteiset(baseId, id);
        return ResponseEntity.ok(yhteisetDto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Timed
    public ResponseEntity<YhteisetDto> updateYhteiset(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @RequestBody(required = false) YhteisetDto body) {
        return ResponseEntity.ok(yhteisetService.updateYhteiset(baseId, id, body));
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<PoistettuDto>> getYhteisetPoistetut(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getYhteisetPoistetut(baseId, id));
    }

    @RequestMapping(value = "/sisalto", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<YhteisetSisaltoDto> getYhteisetSisalto(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getYhteisetSisalto(baseId, id));
    }

    @Override
    public RevisionService getService() {
        return yhteisetService;
    }

    @Override
    public TekstiKappaleViiteService service() {
        return tkvService;
    }
}
