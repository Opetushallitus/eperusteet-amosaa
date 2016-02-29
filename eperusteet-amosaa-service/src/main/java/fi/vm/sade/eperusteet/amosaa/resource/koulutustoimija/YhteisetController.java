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

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.YhteisetService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{koulutustoimijaId}/yhteiset/{id}")
@Api(value = "yhteiset")
public class YhteisetController {

    @Autowired
    private YhteisetService yhteisetService;

    @Autowired
    private TekstiKappaleViiteService tkvService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<YhteisetDto> getYhteiset(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        YhteisetDto yhteisetDto = yhteisetService.getYhteiset(koulutustoimijaId, id);
        return ResponseEntity.ok(yhteisetDto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @Timed
    public ResponseEntity<YhteisetDto> updateYhteiset(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id,
            @RequestBody(required = false) YhteisetDto body) {
        return ResponseEntity.ok(yhteisetService.updateYhteiset(koulutustoimijaId, id, body));
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<List<PoistettuDto>> getYhteisetPoistetut(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getYhteisetPoistetut(koulutustoimijaId, id));
    }

    @RequestMapping(value = "/sisalto", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<YhteisetSisaltoDto> getYhteisetSisalto(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getYhteisetSisalto(koulutustoimijaId, id));
    }

    @RequestMapping(value = "/versiot/uusin", method = RequestMethod.GET)
    @ResponseBody
    @InternalApi
    @Timed
    public ResponseEntity<Revision> getLatestRevision(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getLatestRevision(koulutustoimijaId, id));
    }

    @RequestMapping(value = "/versiot", method = RequestMethod.GET)
    @ResponseBody
    @InternalApi
    @Timed
    public ResponseEntity<List<Revision>> getRevisions(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(yhteisetService.getRevisions(koulutustoimijaId, id));
    }

    @RequestMapping(value = "/versiot/{revId}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<Object> getRevisions(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id,
            @PathVariable("revId") final Integer revId) {
        return ResponseEntity.ok(yhteisetService.getData(koulutustoimijaId, id, revId));
    }

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<TekstiKappaleViiteDto.Matala> getTekstit(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id,
            @PathVariable("tkvId") final Long tkvId) {
        return ResponseEntity.ok(tkvService.getTekstiKappaleViite(koulutustoimijaId, id, tkvId));
    }

    @RequestMapping(value = "/tekstit/otsikot", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<List<TekstiKappaleViiteKevytDto>> getOtsikot(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(tkvService.getTekstiKappaleViitteet(koulutustoimijaId, id, TekstiKappaleViiteKevytDto.class));
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.POST)
    @ResponseBody
    @Timed
    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappaleLapsi(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody(required = false) TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return ResponseEntity.ok(tkvService.addTekstiKappaleViite(koulutustoimijaId, id, viiteId, tekstiKappaleViiteDto));
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.PUT)
    @Timed
    public ResponseEntity<TekstiKappaleViiteDto> updateTekstiKappaleViite(
            @PathVariable("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        return ResponseEntity.ok(tkvService.updateTekstiKappaleViite(koulutustoimijaId, id, viiteId, tekstiKappaleViiteDto));
    }
}
