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
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.RAKENNE_MUOKKAUS;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_LISAYS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_POISTO;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_KLOONAUS;


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
        LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_LISAYS, tekstiKappaleViiteDto).log();
        return service.addSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void copyMultiple(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet) {
        LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_KLOONAUS).log();
        service.copySisaltoViiteet(ktId, opsId, viitteet);
    }

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.PUT)
    void updateTekstiKappaleViite(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto tekstiKappaleViiteDto) {
        LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_MUOKKAUS, tekstiKappaleViiteDto).log();
        service.updateSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{svId}/rakenne", method = RequestMethod.PUT)
    void updateSisaltoViiteRakenne(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto.Puu tekstiKappaleViiteDto) {
        LogMessage.builder(OPETUSSUUNNITELMA, RAKENNE_MUOKKAUS, tekstiKappaleViiteDto).log();
        service.reorderSubTree(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeSisaltoViite(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_POISTO).log();
        service.removeSisaltoViite(ktId, opsId, svId);
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
