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

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OPETUSSUUNNITELMA_LISAYS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OPETUSSUUNNITELMA_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_PALAUTUS;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TILA_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OIKEUS_MUOKKAUS;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat")
@Api(value = "opetussuunnitelmat")
public class OpetussuunnitelmaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private OpetussuunnitelmaService service;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private SisaltoViiteService sisaltoviiteService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaBaseDto> getAll(
            @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(name = "peruste", required = false) Long perusteId) {
        return service.getOpetussuunnitelmat(ktId, perusteId);
    }

    @RequestMapping(value = "/ystavien", method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaDto> getAllOtherOrgs(@ModelAttribute("solvedKtId") final Long ktId) {
        return service.getOtherOpetussuunnitelmat(ktId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public OpetussuunnitelmaBaseDto add(
            @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody OpetussuunnitelmaDto opsDto) {
        LogMessage.builder(OPETUSSUUNNITELMA, OPETUSSUUNNITELMA_LISAYS, opsDto).log();
        return service.addOpetussuunnitelma(ktId, opsDto);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    public OpetussuunnitelmaDto get(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelma(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/peruste", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPeruste(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelmanPeruste(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    @ResponseBody
    public OpetussuunnitelmaDto update(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) OpetussuunnitelmaDto body) {
        LogMessage.builder(OPETUSSUUNNITELMA, OPETUSSUUNNITELMA_MUOKKAUS, body).log();
        return service.update(ktId, opsId, body);
    }

    @RequestMapping(value = "/{opsId}/poistetut", method = RequestMethod.GET)
    @ResponseBody
    public List<PoistettuDto> getPoistetut(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return poistetutService.poistetut(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/poistetut/{poistettuId}/palauta", method = RequestMethod.POST)
    @ResponseBody
    public SisaltoViiteDto getPoistetut(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId) {
        LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_PALAUTUS).log();
        return sisaltoviiteService.restoreSisaltoViite(ktId, opsId, poistettuId);
    }

    @RequestMapping(value = "/{opsId}/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getOikeudet(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOikeudet(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/oikeudet/{kayttajaId}", method = RequestMethod.POST)
    public KayttajaoikeusDto updateOikeus(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long kayttajaId,
            @RequestBody(required = false) KayttajaoikeusDto body) {
        LogMessage.builder(OPETUSSUUNNITELMA, OIKEUS_MUOKKAUS).log();
        return service.updateOikeus(ktId, opsId, kayttajaId, body);
    }

    @RequestMapping(value = "/{opsId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getLatestRevision(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getLatestRevision(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/validoi", method = RequestMethod.GET)
    @InternalApi
    Validointi validoi(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.validoi(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<Revision> getRevisions(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getRevisions(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    Object getRevisions(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Integer revId) {
        return service.getData(ktId, opsId, revId);
    }

    @RequestMapping(value = "/{opsId}/tila/{tila}", method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto updateTila(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Tila tila) {
        LogMessage.builder(OPETUSSUUNNITELMA, TILA_MUOKKAUS).log();
        return service.updateTila(ktId, opsId, tila);
    }
}
