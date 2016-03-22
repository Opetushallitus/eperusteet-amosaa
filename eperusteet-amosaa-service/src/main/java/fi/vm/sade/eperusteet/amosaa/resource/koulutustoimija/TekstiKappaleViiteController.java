package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

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

import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Api(value = "tekstit")
public class TekstiKappaleViiteController {

    @Autowired
    private TekstiKappaleViiteService service;

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.GET)
    TekstiKappaleViiteDto.Matala getTekstit(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getTekstiKappaleViite(ktId, opsId, tkvId);
    }

    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    List<TekstiKappaleViiteKevytDto> getOtsikot(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getTekstiKappaleViitteet(ktId, opsId, TekstiKappaleViiteKevytDto.class);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.POST)
    TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody(required = false) TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return service.addTekstiKappaleViite(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.PUT)
    void updateTekstiKappaleViite(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        service.updateTekstiKappaleViite(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/rakenne", method = RequestMethod.PUT)
    void updateTekstiKappaleViiteRakenne(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        service.reorderSubTree(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeTekstiKappaleViite(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        service.removeTekstiKappaleViite(ktId, opsId, tkvId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getLatestRevision(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getLatestRevision(ktId, opsId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<Revision> getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getRevisions(ktId, opsId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    Object getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @PathVariable final Integer revId) {
        return service.getData(ktId, opsId, revId);
    }
}
