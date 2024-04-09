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

package fi.vm.sade.eperusteet.amosaa.resource.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaDto;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliTunnisteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaSuoritustapaDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/api/perusteet")
@Api(value = "perusteet")
@InternalApi
public class PerusteController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService service;

    @Autowired
    private EperusteetClient eperusteet;

    @RequestMapping(value = "/haku", method = RequestMethod.GET)
    public JsonNode haePerusteista(@RequestParam  Map<String, String> params) {
        return eperusteet.findFromPerusteet(params);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<PerusteDto> getPerusteet() {
        return service.findPerusteet();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PerusteDto getPeruste(@PathVariable Long id) {
        return service.getPerusteSisalto(id, PerusteDto.class);
    }

    @RequestMapping(value = "/{id}/kaikki", method = RequestMethod.GET)
    public JsonNode getPerusteAll(@PathVariable Long id) {
        return service.getPerusteSisalto(id, JsonNode.class);
    }

    @RequestMapping(value = "/{id}/tutkinnonosat", method = RequestMethod.GET)
    public JsonNode getPerusteTutkinnonOsat(@PathVariable Long id) {
        return service.getTutkinnonOsat(id);
    }

    @RequestMapping(value = "/{id}/suorituspolkuosat", method = RequestMethod.GET)
    public List<TutkinnonOsaSuoritustapaDto> getPerusteTutkinnonOsatKevyt(@PathVariable Long id) {
        List<TutkinnonOsaSuoritustapaDto> tosat = service.convertTutkinnonOsat(service.getTutkinnonOsat(id));
        return tosat;
    }

    @RequestMapping(value = "/{id}/tutkinnonosat/{tosaId}", method = RequestMethod.GET)
    public JsonNode getPerusteTutkinnonOsa(
            @PathVariable Long id,
            @PathVariable Long tosaId) {
        return service.getTutkinnonOsa(id, tosaId);
    }

    @RequestMapping(value = "/{id}/suoritustavat", method = RequestMethod.GET)
    public JsonNode getPerusteRakenteet(@PathVariable Long id) {
        return service.getSuoritustavat(id);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}", method = RequestMethod.GET)
    public JsonNode getPerusteRakenne(
            @PathVariable Long id,
            @PathVariable String st) {
        return service.getSuoritustapa(id, st);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}/tutkinnonosat/{tosaId}", method = RequestMethod.GET)
    public JsonNode getTutkinnonOsaViite(
            @PathVariable Long id,
            @PathVariable String st,
            @PathVariable Long tosaId
    ) {
        return service.getTutkinnonOsaViite(id, st, tosaId);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}/tutkinnonosat", method = RequestMethod.GET)
    public JsonNode getTutkinnonOsaViitteet(
            @PathVariable Long id,
            @PathVariable String st
    ) {
        return service.getTutkinnonOsaViitteet(id, st);
    }

    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@RequestParam String diaarinumero) {
        return opetussuunnitelmaService.getPerusteenOpetussuunnitelmat(diaarinumero, OpetussuunnitelmaBaseDto.class);
    }

    @RequestMapping(value = "/{perusteId}/perusteesta", method = RequestMethod.GET)
    public JsonNode getPerusteByPerusteId(@PathVariable Long perusteId) {
        return service.getPerusteSisaltoByPerusteId(perusteId, JsonNode.class);
    }

    @RequestMapping(value = "/{perusteId}/perusteenosa/{perusteenosaId}", method = RequestMethod.GET)
    public PerusteenOsaDto getPerusteenOsa(@PathVariable Long perusteId, @PathVariable Long perusteenosaId) {
        return service.getPerusteenOsa(perusteId, perusteenosaId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimija/{ktId}/opetussuunnitelma/{opetussuunnitelmaId}/koulutuskoodillakorvaava", method = RequestMethod.GET)
    public PerusteDto getKoulutuskoodillaKorvaavaPeruste(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opetussuunnitelmaId
    ) {
        return service.getKoulutuskoodillaKorvaavaPeruste(ktId, opetussuunnitelmaId);
    }
    
//    @RequestMapping(value = "/{perusteId}/tutkintonimikekoodit", method = GET)
//    @InternalApi
//    public ResponseEntity<List<CombinedDto<TutkintonimikeKoodiDto, HashMap<String, KoodistoKoodiDto>>>> getTutkintonimikekoodit(@PathVariable("perusteId") final long id) {
//        List<TutkintonimikeKoodiDto> tutkintonimikeKoodit = service.getTutkintonimikeKoodit(id);
//        List<CombinedDto<TutkintonimikeKoodiDto, HashMap<String, KoodistoKoodiDto>>> response = new ArrayList<>();
//
//        for (TutkintonimikeKoodiDto tkd : tutkintonimikeKoodit) {
//            HashMap<String, KoodistoKoodiDto> nimet = new HashMap<>();
//            if (tkd.getOsaamisalaUri() != null) {
//                nimet.put(tkd.getOsaamisalaArvo(), koodistoService.get("osaamisala", tkd.getOsaamisalaUri()));
//            }
//            nimet.put(tkd.getTutkintonimikeArvo(), koodistoService.get("tutkintonimikkeet", tkd.getTutkintonimikeUri()));
//            if (tkd.getTutkinnonOsaUri() != null) {
//                nimet.put(tkd.getTutkinnonOsaArvo(), koodistoService.get("tutkinnonosat", tkd.getTutkinnonOsaUri()));
//            }
//            response.add(new CombinedDto<>(tkd, nimet));
//        }
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
