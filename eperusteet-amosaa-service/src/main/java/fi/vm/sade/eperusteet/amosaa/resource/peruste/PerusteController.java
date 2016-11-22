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

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/perusteet")
@Api(value = "perusteet")
public class PerusteController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService service;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PerusteDto getPeruste(@PathVariable Long id) {
        return service.getPerusteSisalto(id, PerusteDto.class);
    }

    @RequestMapping(value = "/{id}/kaikki", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPerusteAll(@PathVariable Long id) {
        return service.getPerusteSisalto(id, JsonNode.class);
    }

    @RequestMapping(value = "/{id}/tutkinnonosat", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPerusteTutkinnonOsat(@PathVariable Long id) {
        return service.getTutkinnonOsat(id);
    }

    @RequestMapping(value = "/{id}/tutkinnonosat/{tosaId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPerusteTutkinnonOsa(
            @PathVariable Long id,
            @PathVariable Long tosaId) {
        return service.getTutkinnonOsa(id, tosaId);
    }

    @RequestMapping(value = "/{id}/suoritustavat", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPerusteRakenne(@PathVariable Long id) {
        return service.getSuoritustavat(id);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{suoritustapa}", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode getPerusteRakenne(
            @PathVariable Long id,
            @PathVariable String st) {
        return service.getSuoritustapa(id, st);
    }

    @RequestMapping(value = "opetussuunnitelmat", method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@RequestParam String diaarinumero) {
        return opetussuunnitelmaService.getPerusteenOpetussuunnitelmat(diaarinumero);
    }

//    @RequestMapping(value = "/{perusteId}/tutkintonimikekoodit", method = GET)
//    @ResponseBody
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
