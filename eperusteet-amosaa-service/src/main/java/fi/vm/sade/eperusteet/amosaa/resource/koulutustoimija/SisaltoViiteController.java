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
import fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaAudit;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.*;


/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Api(value = "sisältö")
public class SisaltoViiteController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private SisaltoViiteService service;

    @Autowired
    private EperusteetAmosaaAudit audit;

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.GET)
    SisaltoViiteDto.Matala getTekstit(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        return service.getSisaltoViite(ktId, opsId, svId);
    }

    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    List<SisaltoViiteKevytDto> getOtsikot(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @RequestMapping(value = "/suorituspolut", method = RequestMethod.GET)
    List<SisaltoViiteDto> getSuorituspolut(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getSuorituspolut(ktId, opsId, SisaltoViiteDto.class);
    }

    @RequestMapping(value = "/tutkinnonosat", method = RequestMethod.GET)
    List<SisaltoViiteDto> getTutkinnonosat(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getTutkinnonOsat(ktId, opsId, SisaltoViiteDto.class);
    }

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.POST)
    SisaltoViiteDto.Matala addTekstiKappaleLapsi(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody(required = false) SisaltoViiteDto.Matala tekstiKappaleViiteDto) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_LISAYS, tekstiKappaleViiteDto), (Void) -> {
            return service.addSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
        });
    }

    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void copyMultiple(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet) {
        audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_KLOONAUS), (Void) -> {
            service.copySisaltoViiteet(ktId, opsId, viitteet);
            return null;
        });
    }

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.PUT)
    void updateTekstiKappaleViite(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto tekstiKappaleViiteDto) {
        audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_MUOKKAUS, tekstiKappaleViiteDto), (Void) -> {
            service.updateSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
            return null;
        });
    }

    @RequestMapping(value = "/tekstit/{svId}/rakenne", method = RequestMethod.PUT)
    void updateSisaltoViiteRakenne(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto.Puu tekstiKappaleViiteDto) {
        audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, RAKENNE_MUOKKAUS, tekstiKappaleViiteDto), (Void) -> {
            service.reorderSubTree(ktId, opsId, svId, tekstiKappaleViiteDto);
            return null;
        });
    }

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeSisaltoViite(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_POISTO), (Void) -> {
            service.removeSisaltoViite(ktId, opsId, svId);
            return null;
        });
    }

    @RequestMapping(value = "/tekstit/{svId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    RevisionDto getLatestRevision(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        return service.getLatestRevision(ktId, opsId, svId);
    }

    @RequestMapping(value = "/tekstit/{svId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<RevisionDto> getRevisions(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        return service.getRevisions(ktId, opsId, svId);
    }

    @RequestMapping(value = "/tekstit/{svId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    SisaltoViiteDto getRevisions(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @PathVariable final Integer revId) {
        return service.getData(ktId, opsId, svId, revId);
    }
}
