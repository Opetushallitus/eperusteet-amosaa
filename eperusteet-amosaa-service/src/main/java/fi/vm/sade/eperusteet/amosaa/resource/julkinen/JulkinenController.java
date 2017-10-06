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

package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/julkinen")
@Api(value = "julkinen")
public class JulkinenController {

    @Autowired
    private KoulutustoimijaService ktService;

    @Autowired
    private SisaltoViiteService svService;

    @Autowired
    private OpetussuunnitelmaService opsService;

    @Autowired
    private TiedoteService tiedoteService;

    @RequestMapping(value = "/tutkinnonosat/{koodi}", method = RequestMethod.GET)
    public SisaltoViiteDto getTutkinnonOsa(@PathVariable final String koodi) {
        throw new UnsupportedOperationException("ei-toteutettu-viela");
    }

    @RequestMapping(value = "/opetussuunnitelmat/{opsId}/koulutustoimija", method = RequestMethod.GET)
    public KoulutustoimijaJulkinenDto getOpetussuunnitelmanToimija(@PathVariable final Long opsId) {
        return opsService.getKoulutustoimijaId(opsId);
    }

    @RequestMapping(value = "/koulutustoimijat/org/{ktOid}", method = RequestMethod.GET)
    public KoulutustoimijaJulkinenDto getKoulutustoimija(@PathVariable final String ktOid) {
        return ktService.getKoulutustoimijaJulkinen(ktOid);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}", method = RequestMethod.GET)
    public KoulutustoimijaJulkinenDto getKoulutustoimija(@PathVariable("ktId") final Long ktId) {
        return ktService.getKoulutustoimijaJulkinen(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sivu", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(
            @ApiIgnore OpetussuunnitelmaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        return opsService.findOpetussuunnitelmat(p, pquery);
    }

    @RequestMapping(value = "/perusteenopetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaJulkinenDto> getPerusteenOpetussuunnitelmat(
            @RequestParam final String perusteenDiaarinumero
    ) {
        return opsService.getJulkaistutPerusteenOpetussuunnitelmat(perusteenDiaarinumero, OpetussuunnitelmaJulkinenDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sivu", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(
            @ApiIgnore KoulutustoimijaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        return ktService.findKoulutustoimijat(p, pquery);
    }

    @RequestMapping(value = "/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(@PathVariable final String koodi) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(null, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final String koodi
    ) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(ktId, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return opsService.getJulkisetOpetussuunnitelmat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/tiedotteet", method = RequestMethod.GET)
    public List<TiedoteDto> getJulkisetTiedotteet(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return tiedoteService.getJulkisetTiedotteet(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto get(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelma(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/otsikot", method = RequestMethod.GET)
    List<SisaltoViiteKevytDto> getOtsikot(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return svService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{svId}", method = RequestMethod.GET)
    SisaltoViiteDto.Matala getTekstit(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return svService.getSisaltoViite(ktId, opsId, svId);
    }
}
