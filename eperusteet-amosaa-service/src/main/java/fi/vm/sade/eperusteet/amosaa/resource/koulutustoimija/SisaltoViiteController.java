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

import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
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
@Api(value = "sisältö")
public class SisaltoViiteController {

    @Autowired
    private SisaltoViiteService service;

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.GET)
    SisaltoViiteDto.Matala getTekstit(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getSisaltoViite(ktId, opsId, tkvId);
    }

    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    List<SisaltoViiteKevytDto> getOtsikot(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.POST)
    SisaltoViiteDto.Matala addTekstiKappaleLapsi(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody(required = false) SisaltoViiteDto.Matala tekstiKappaleViiteDto) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return service.addSisaltoViite(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.PUT)
    void updateTekstiKappaleViite(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody final SisaltoViiteDto tekstiKappaleViiteDto) {
        service.updateSisaltoViite(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/rakenne", method = RequestMethod.PUT)
    void updateSisaltoViiteRakenne(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @RequestBody final SisaltoViiteDto.Puu tekstiKappaleViiteDto) {
        service.reorderSubTree(ktId, opsId, tkvId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeSisaltoViite(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        service.removeSisaltoViite(ktId, opsId, tkvId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    RevisionDto getLatestRevision(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getLatestRevision(opsId, tkvId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<RevisionDto> getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId) {
        return service.getRevisions(opsId, tkvId);
    }

    @RequestMapping(value = "/tekstit/{tkvId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    SisaltoViiteDto getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long tkvId,
            @PathVariable final Integer revId) {
        return service.getData(opsId, tkvId, revId);
    }
}
