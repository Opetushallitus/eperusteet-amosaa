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
import fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaAudit;

import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OIKEUS_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OPETUSSUUNNITELMA_LISAYS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.OPETUSSUUNNITELMA_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_PALAUTUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TILA_MUOKKAUS;

import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import io.swagger.annotations.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
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

    @Autowired
    private EperusteetAmosaaAudit audit;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getAll(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(name = "peruste", required = false) Long perusteId
    ) {
        return service.getOpetussuunnitelmat(ktId, perusteId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/ystavien", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOtherOrgs(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return service.getOtherOpetussuunnitelmat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto add(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody OpetussuunnitelmaDto opsDto
    ) {
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, OPETUSSUUNNITELMA_LISAYS, opsDto),
                (Void) -> service.addOpetussuunnitelma(ktId, opsDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto get(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOpetussuunnitelma(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/peruste", method = RequestMethod.GET)
    public JsonNode getPeruste(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelmanPeruste(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    public OpetussuunnitelmaDto update(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) OpetussuunnitelmaDto body
    ) {
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, OPETUSSUUNNITELMA_MUOKKAUS, body),
                (Void) -> service.update(ktId, opsId, body));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/poistetut", method = RequestMethod.GET)
    public List<PoistettuDto> getPoistetut(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return poistetutService.poistetut(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/poistetut/{poistettuId}/palauta", method = RequestMethod.POST)
    public SisaltoViiteDto getPoistetut(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId
    ) {
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, SISALTO_PALAUTUS),
                (Void) -> sisaltoviiteService.restoreSisaltoViite(ktId, opsId, poistettuId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getOikeudet(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOikeudet(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/oikeudet/{kayttajaId}", method = RequestMethod.POST)
    public KayttajaoikeusDto updateOikeus(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long kayttajaId,
            @RequestBody(required = false) KayttajaoikeusDto body
    ) {
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, OIKEUS_MUOKKAUS),
                (Void) -> service.updateOikeus(ktId, opsId, kayttajaId, body));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getLatestRevision(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getLatestRevision(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/validoi", method = RequestMethod.GET)
    @InternalApi
    Validointi validoi(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.validoi(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<Revision> getRevisions(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getRevisions(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    Object getRevisions(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Integer revId
    ) {
        return service.getData(ktId, opsId, revId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/tila/{tila}", method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto updateTila(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Tila tila
    ) {
        return audit.withAudit(LogMessage.builder(OPETUSSUUNNITELMA, TILA_MUOKKAUS),
                (Void) -> service.updateTila(ktId, opsId, tila));
    }
}
