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
import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private PermissionManager permissionManager;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<KoulutustoimijaDto> get(@PathVariable("id") final Long id) {
        KoulutustoimijaDto koulutustoimijaDto = koulutustoimijaService.getKoulutustoimija(id);
        if (koulutustoimijaDto != null) {
            return new ResponseEntity<>(koulutustoimijaService.getKoulutustoimija(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<List<OpetussuunnitelmaBaseDto>> getOpetussuunnitelmat(
            @PathVariable("id") final Long id) {
        return new ResponseEntity<>(koulutustoimijaService.getOpetussuunnitelmat(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<OpetussuunnitelmaDto> getOpetussuunnitelma(
            @PathVariable("id") final Long id,
            @PathVariable("opsId") final Long opsId) {
        return new ResponseEntity<>(koulutustoimijaService.getOpetussuunnitelma(id, opsId), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/tiedotteet", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<List<TiedoteDto>> getTiedotteet(
            @PathVariable("id") final Long id) {
        return new ResponseEntity<>(koulutustoimijaService.getTiedotteet(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/tiedote/{tiedoteId}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<TiedoteDto> getTiedote(
            @PathVariable("id") final Long id,
            @PathVariable("tiedoteId") final Long tiedoteId) {
        return new ResponseEntity<>(koulutustoimijaService.getTiedote(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/omattiedotteet", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<List<TiedoteDto>> getOmatTiedotteet(
            @PathVariable("id") final Long id) {
        return new ResponseEntity<>(koulutustoimijaService.getOmatTiedotteet(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/oikeudet", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<Map<PermissionManager.TargetType, Set<PermissionManager.Permission>>> getOikeudet() {
        return new ResponseEntity<>(permissionManager.getOpsPermissions(), HttpStatus.OK);
    }

}
