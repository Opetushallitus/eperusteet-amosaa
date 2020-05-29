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
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliTunnisteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat")
@Api(value = "opetussuunnitelmat")
@InternalApi
public class OpetussuunnitelmaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private OpetussuunnitelmaService service;

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private SisaltoViiteService sisaltoviiteService;
    
    @Autowired
    private EperusteetService eperusteetService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.GET)
    public Page<OpetussuunnitelmaBaseDto> getAllOpetussuunnitelmatBaseSivutettu(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @ApiIgnore OpsHakuDto query
    ) {
        PageRequest pageRequest = new PageRequest(query.getSivu(), Math.min(query.getSivukoko(), 25));
        return service.getOpetussuunnitelmat(ktId, pageRequest, query);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaDto> getAllOpetussuunnitelmatSivutettu(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @ApiIgnore OpsHakuDto query
    ) {
        PageRequest pageRequest = new PageRequest(query.getSivu(), Math.min(query.getSivukoko(), 25));
        return service.getOpetussuunnitelmat(ktId, pageRequest, query, OpetussuunnitelmaDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/vanhentuneet", method = RequestMethod.GET)
    public List<VanhentunutPohjaperusteDto> getPaivitettavatOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return service.haePaivitystaVaativatPerusteet(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/paivita", method = RequestMethod.POST)
    public void paivitaOpetussuunnitelmanPeruste(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        service.paivitaPeruste(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/ystavien", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOtherOrgsOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return service.getOtherOpetussuunnitelmat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody OpetussuunnitelmaLuontiDto opsDto
    ) {
        return service.addOpetussuunnitelma(ktId, opsDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto getOpetussuunnitelma(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOpetussuunnitelma(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/peruste", method = RequestMethod.GET)
    public JsonNode getOpetussuunnitelmaPeruste(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelmanPeruste(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    public OpetussuunnitelmaDto updateOpetussuunnitelma(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) OpetussuunnitelmaDto body
    ) {
        return service.update(ktId, opsId, body);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/poistetut", method = RequestMethod.GET)
    public List<PoistettuDto> getPoistetutOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return poistetutService.poistetut(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/poistetut/{poistettuId}/palauta", method = RequestMethod.POST)
    public SisaltoViiteDto palautaOpetussuunnitelmaSisaltoviite(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId
    ) {
        return sisaltoviiteService.restoreSisaltoViite(ktId, opsId, poistettuId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getOpetussuunnitelmaOikeudet(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOikeudet(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/oikeudet/{kayttajaId}", method = RequestMethod.POST)
    public KayttajaoikeusDto updateOpetussuunnitelmaOikeus(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long kayttajaId,
            @RequestBody(required = false) KayttajaoikeusDto body
    ) {
        return service.updateOikeus(ktId, opsId, kayttajaId, body);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getOpetussuunnitelmaLatestRevision(
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
    Validointi validoiOpetussuunnitelma(
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
    List<Revision> getOpetussuunnitelmaRevisions(
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
    Object getOpetussuunnitelmaRevision(
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
    public OpetussuunnitelmaBaseDto updateOpetussuunnitelmaTila(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Tila tila
    ) {
        return service.updateTila(ktId, opsId, tila, true);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/siirra", method = RequestMethod.POST)
    public OpetussuunnitelmaDto updateOpetussuunnitelmaKoulutustoimija(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) KoulutustoimijaBaseDto body
    ) {
        return service.updateKoulutustoimija(ktId, opsId, body);
    }
    

    @ApiImplicitParams({
        @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/siirraPassivoidusta", method = RequestMethod.POST)
    public OpetussuunnitelmaDto updateOpetussuunnitelmaKoulutustoimijaPassivoidusta(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.updateKoulutustoimijaPassivoidusta(ktId, opsId);
    }
    
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/organisaatio/{organisaatioid}", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String organisaatioid
    ) {
        return service.getOpetussuunnitelmatOrganisaatioista(organisaatioid);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/suorituspolku", method = RequestMethod.GET)
    public List<RakenneModuuliTunnisteDto> getPerusteRakenneNakyvat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId) {
        return eperusteetService.getSuoritustavat(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{opsId}/navigaatio", method = GET)
    public NavigationNodeDto getOpetussuunnitelmaNavigation(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.buildNavigation(ktId, opsId);
    }
}
