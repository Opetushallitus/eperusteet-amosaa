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

package fi.vm.sade.eperusteet.amosaa.resource.util;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koodisto")
@InternalApi
public class KoodistoController {
    @Autowired
    KoodistoClient service;

    @RequestMapping(value = "/{koodisto}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> kaikki(
        @PathVariable final String koodisto,
        @RequestParam(value = "haku", required = false) final String haku) {
        return new ResponseEntity<>(haku == null || haku.isEmpty()
                ? service.getAll(koodisto)
                : service.filterBy(koodisto, haku), HttpStatus.OK);
    }

    @RequestMapping(value = "/{koodisto}/{koodi}", method = GET)
    public ResponseEntity<KoodistoKoodiDto> yksi(
        @PathVariable final String koodisto,
        @PathVariable final String koodi) {
        return new ResponseEntity<>(service.get(koodisto, koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/{koodisto}/haku/{query}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> query(
        @PathVariable final String koodisto,
        @PathVariable final String query) {
        return new ResponseEntity<>(service.queryByKoodi(koodisto, query), HttpStatus.OK);
    }

    @RequestMapping(value = "/uri/{koodiUri}", method = GET)
    public ResponseEntity<KoodistoKoodiDto> byUri(
        @PathVariable final String koodiUri) {
        return new ResponseEntity<>(service.getByUri(koodiUri), HttpStatus.OK);
    }

    @RequestMapping(value = "/relaatio/sisaltyy-alakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> alarelaatio(
        @PathVariable final String koodi) {
        return new ResponseEntity<>(service.getAlarelaatio(koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/relaatio/sisaltyy-ylakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> ylarelaatio(
        @PathVariable final String koodi) {
        return new ResponseEntity<>(service.getYlarelaatio(koodi), HttpStatus.OK);
    }
}
