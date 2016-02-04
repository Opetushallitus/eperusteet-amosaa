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

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.TyoryhmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/{koulutustoimija}", method = RequestMethod.GET)
    @ResponseBody
    public KoulutustoimijaDto get(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getKoulutustoimija(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/yhteinen", method = RequestMethod.GET)
    @ResponseBody
    public YhteinenDto getYhteinen(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getYhteinen(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/yhteinen/poistetut", method = RequestMethod.GET)
    @ResponseBody
    public List<PoistettuDto> getYhteinenPoistetut(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getYhteinenPoistetut(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/yhteinen/tyoryhmat", method = RequestMethod.GET)
    @ResponseBody
    public List<TyoryhmaDto> getYhteinenTyoryhmat(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getTyoryhmat(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/yhteinen/sisalto", method = RequestMethod.GET)
    @ResponseBody
    public YhteinenSisaltoDto getYhteinenSisalto(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getYhteinenSisalto(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/opetussuunnitelmat", method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getOpetussuunnitelmat(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    public OpetussuunnitelmaDto getOpetussuunnitelma(
            @PathVariable("koulutustoimija") final String kOid,
            @PathVariable("opsId") final Long opsId) {
        return koulutustoimijaService.getOpetussuunnitelma(kOid, opsId);
    }

    @RequestMapping(value = "/{koulutustoimija}/tiedotteet", method = RequestMethod.GET)
    @ResponseBody
    public List<TiedoteDto> getTiedotteet(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getTiedotteet(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/tiedote/{tiedoteId}", method = RequestMethod.GET)
    @ResponseBody
    public TiedoteDto getTiedote(
            @PathVariable("koulutustoimija") final String kOid,
            @PathVariable("tiedoteId") final Long tiedoteId) {
        return koulutustoimijaService.getTiedote(kOid);
    }

    @RequestMapping(value = "/{koulutustoimija}/omattiedotteet", method = RequestMethod.GET)
    @ResponseBody
    public List<TiedoteDto> getOmatTiedotteet(
            @PathVariable("koulutustoimija") final String kOid) {
        return koulutustoimijaService.getOmatTiedotteet(kOid);
    }
}
