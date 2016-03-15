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

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat")
@Api(value = "koulutustoimijat")
public class KoulutustoimijaController {
    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public KoulutustoimijaDto get(
            @PathVariable final Long id) {
        return koulutustoimijaService.getKoulutustoimija(id);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat", method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(
            @PathVariable final Long id) {
        return koulutustoimijaService.getOpetussuunnitelmat(id);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    public OpetussuunnitelmaDto getOpetussuunnitelma(
            @PathVariable final Long id,
            @PathVariable final Long opsId) {
        return koulutustoimijaService.getOpetussuunnitelma(id, opsId);
    }

    @RequestMapping(value = "/{id}/tiedotteet", method = RequestMethod.GET)
    @ResponseBody
    public List<TiedoteDto> getTiedotteet(
            @PathVariable final Long id) {
        return koulutustoimijaService.getTiedotteet(id);
    }

    @RequestMapping(value = "/{id}/tiedote/{tiedoteId}", method = RequestMethod.GET)
    @ResponseBody
    public TiedoteDto getTiedote(
            @PathVariable final Long id,
            @PathVariable final Long tiedoteId) {
        return koulutustoimijaService.getTiedote(id);
    }

    @RequestMapping(value = "/{id}/omattiedotteet", method = RequestMethod.GET)
    @ResponseBody
    public List<TiedoteDto> getOmatTiedotteet(
            @PathVariable final Long id) {
        return koulutustoimijaService.getOmatTiedotteet(id);
    }

    @RequestMapping(value = "/kayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajanTietoDto>> getKayttajat(
            @PathVariable final Long id) {
        return new ResponseEntity<>(koulutustoimijaService.getKayttajat(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/kayttajat/{oid}", method = RequestMethod.GET)
    public ResponseEntity<KayttajanTietoDto> getKayttajat(
            @PathVariable final Long id,
            @PathVariable final String oid) {
        return ResponseEntity.ok(koulutustoimijaService.getKayttaja(id, oid));
    }
}
