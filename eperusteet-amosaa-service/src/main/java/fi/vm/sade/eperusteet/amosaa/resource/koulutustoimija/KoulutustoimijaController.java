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

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViitePaikallinenIntegrationDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import java.util.List;
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

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private KayttajanTietoService kayttajaTietoService;

    @RequestMapping(value = "/{ktId}", method = RequestMethod.GET)
    @ResponseBody
    public KoulutustoimijaDto get(
            @PathVariable final Long ktId) {
        return koulutustoimijaService.getKoulutustoimija(ktId);
    }

    @RequestMapping(value = "/{ktId}/tutkinnonosat", method = RequestMethod.GET)
    @ResponseBody
    public List<SisaltoViitePaikallinenIntegrationDto> getTutkinnonosat(
            @PathVariable final Long ktId) {
        return koulutustoimijaService.getPaikallisetTutkinnonOsat(ktId, SisaltoViitePaikallinenIntegrationDto.class);
    }

    @RequestMapping(value = "/{ktId}/kayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaDto>> getKayttajat(
            @PathVariable final Long ktId) {
        return new ResponseEntity<>(kayttajaTietoService.getKayttajat(ktId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{ktId}/kayttajat/{kayttajaOid}", method = RequestMethod.GET)
    public ResponseEntity<KayttajanTietoDto> getKayttajat(
            @PathVariable final Long ktId,
            @PathVariable final String kayttajaOid) {
        return ResponseEntity.ok(kayttajaTietoService.getKayttaja(ktId, kayttajaOid));
    }

    @RequestMapping(value = "/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(
            @PathVariable final Long ktId,
            @PathVariable final String koodi) {
        return new ResponseEntity<>(sisaltoViiteService.getByKoodi(ktId, koodi, SisaltoViiteSijaintiDto.class), HttpStatus.OK);
    }

//    @RequestMapping(value = "/{ktId}", method = RequestMethod.GET)
//    @ResponseBody
//    public List get(
//            @PathVariable final Long ktId) {
//        return koulutustoimijaService.getKoulutustoimija(ktId);
//    }
}
