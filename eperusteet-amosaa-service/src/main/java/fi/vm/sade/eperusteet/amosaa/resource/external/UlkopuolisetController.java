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
package fi.vm.sade.eperusteet.amosaa.resource.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.KoodistoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import springfox.documentation.annotations.ApiIgnore;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/ulkopuoliset")
@InternalApi
public class UlkopuolisetController {

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @RequestMapping(value = "/kayttajatiedot/{oid:.+}", method = GET)
    public ResponseEntity<KayttajanTietoDto> get(@PathVariable final String oid) {
        return new ResponseEntity<>(kayttajanTietoService.hae(oid), HttpStatus.OK);
    }

    @RequestMapping(value = "/julkaistutperusteet", method = GET)
    public ResponseEntity<List<PerusteDto>> getPerusteet() {
        return new ResponseEntity<>(eperusteetService.findPerusteet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tiedotteet", method = GET)
    public ResponseEntity<JsonNode> getTiedotteet(@RequestParam(value = "jalkeen", required = false) final Long jalkeen) {
        return new ResponseEntity<>(eperusteetService.getTiedotteet(jalkeen), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/{koodisto}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> kaikki(
            @PathVariable final String koodisto,
            @RequestParam(value = "haku", required = false) final String haku) {
        return new ResponseEntity<>(haku == null || haku.isEmpty()
                ? koodistoService.getAll(koodisto)
                : koodistoService.filterBy(koodisto, haku), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/{koodisto}/{koodi}", method = GET)
    public ResponseEntity<KoodistoKoodiDto> yksi(
            @PathVariable final String koodisto,
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.get(koodisto, koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-alakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> alarelaatio(
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.getAlarelaatio(koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-ylakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> ylarelaatio(
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.getYlarelaatio(koodi), HttpStatus.OK);
    }
}
