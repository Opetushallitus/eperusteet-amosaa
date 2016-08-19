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

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/julkinen")
@Api(value = "julkinen")
public class JulkinenController {

    @Autowired
    private SisaltoViiteService svService;

    @Autowired
    private OpetussuunnitelmaService opsService;

    @Autowired
    private KoulutustoimijaService ktService;

    @RequestMapping(value = "/tutkinnonosat/{koodi}", method = RequestMethod.GET)
    public SisaltoViiteDto getTutkinnonOsa(@PathVariable final String koodi) {
        throw new UnsupportedOperationException("ei-toteutettu-viela");
    }

    @RequestMapping(value = "/opetussuunnitelmat/{opsId}/koulutustoimija", method = RequestMethod.GET)
    public Long get(@PathVariable final Long opsId) {
        return opsService.getKoulutustoimijaId(opsId);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}", method = RequestMethod.GET)
    @ResponseBody
    public KoulutustoimijaDto getKoulutustoimija(@PathVariable final Long ktId) {
        return ktService.getKoulutustoimijaJulkinen(ktId);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(@PathVariable final Long ktId,
                                                                    @PathVariable final String koodi) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(ktId, koodi, SisaltoViiteSijaintiDto.class));
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto get(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return opsService.getOpetussuunnitelma(opsId);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/otsikot", method = RequestMethod.GET)
    List<SisaltoViiteKevytDto> getOtsikot(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return svService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{svId}", method = RequestMethod.GET)
    SisaltoViiteDto.Matala getTekstit(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        return svService.getSisaltoViite(ktId, opsId, svId);
    }
}
