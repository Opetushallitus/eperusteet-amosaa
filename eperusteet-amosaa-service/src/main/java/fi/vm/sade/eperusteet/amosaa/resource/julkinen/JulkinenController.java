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

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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

    @Autowired
    DokumenttiService service;

    @RequestMapping(value = "/tutkinnonosat/{koodi}", method = RequestMethod.GET)
    @ApiIgnore
    public SisaltoViiteDto getTutkinnonOsa(
            @PathVariable final String koodi
    ) {
        throw new UnsupportedOperationException("ei-toteutettu-viela");
    }

    @RequestMapping(value = "/opetussuunnitelmat/{opsId}/koulutustoimija", method = RequestMethod.GET)
    @Description("Opetussuunnitelman omistavan toimijan tiedot.")
    public KoulutustoimijaJulkinenDto getOpetussuunnitelmanToimija(
            @PathVariable final Long opsId
    ) {
        return opsService.getKoulutustoimijaId(opsId);
    }

    @RequestMapping(value = "/koulutustoimijat/org/{ktOid}", method = RequestMethod.GET)
    @Description("Koulutuksen järjestäjän tiedot organisaation oidin perusteella.")
    public KoulutustoimijaJulkinenDto getKoulutustoimijaByOid(
            @PathVariable final String ktOid
    ) {
        return ktService.getKoulutustoimijaJulkinen(ktOid);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}", method = RequestMethod.GET)
    @Description("Koulutuksen järjestäjän tiedot.")
    public KoulutustoimijaJulkinenDto getKoulutustoimijaByKtId(
            @PathVariable final Long ktId
    ) {
        return ktService.getKoulutustoimijaJulkinen(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien parametrihaku.")
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(
            @ApiIgnore final OpetussuunnitelmaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        return opsService.findOpetussuunnitelmat(p, pquery);
    }

    @RequestMapping(value = "/perusteenopetussuunnitelmat", method = RequestMethod.GET)
    @Description("")
    public List<OpetussuunnitelmaJulkinenDto> getPerusteenOpetussuunnitelmat(
            @RequestParam final String perusteenDiaarinumero
    ) {
        return opsService.getJulkaistutPerusteenOpetussuunnitelmat(perusteenDiaarinumero, OpetussuunnitelmaJulkinenDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(
            @ApiIgnore final KoulutustoimijaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        return ktService.findKoulutustoimijat(p, pquery);
    }

    @RequestMapping(value = "/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getSisaltoViiteByKoodi(
            @PathVariable final String koodi
    ) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(null, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final String koodi
    ) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(ktId, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return opsService.getJulkisetOpetussuunnitelmat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/tiedotteet", method = RequestMethod.GET)
    public List<TiedoteDto> getJulkisetTiedotteet(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return tiedoteService.getJulkisetTiedotteet(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto getOpetussuunnitelmaJulkinen(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelma(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti", method = RequestMethod.GET)
    public ResponseEntity<Object> getDokumentti(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) {
        byte[] pdfdata = service.get(ktId, opsId, Kieli.of(kieli));

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        LokalisoituTekstiDto nimiDto = opsService.getOpetussuunnitelma(ktId, opsId).getNimi();
        String nimi = nimiDto.get(Kieli.of(kieli));
        if (nimi != null) {
            headers.set("Content-disposition", "inline; filename=\"" + nimi + ".pdf\"");
        } else {
            DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
            headers.set("Content-disposition", "inline; filename=\"" + dokumenttiDto.getId() + ".pdf\"");
        }

        headers.setContentLength(pdfdata.length);

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> queryDokumentti(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            dokumenttiDto = service.createDtoFor(ktId, opsId, Kieli.of(kieli));
        }

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kaikki", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelmaKaikki(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/otsikot", method = RequestMethod.GET)
    public List<SisaltoViiteKevytDto> getOpetussuunnitelmaOtsikot(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return svService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{svId}", method = RequestMethod.GET)
    public SisaltoViiteDto.Matala getOpetussuunnitelmaTekstit(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return svService.getSisaltoViite(ktId, opsId, svId);
    }
}
