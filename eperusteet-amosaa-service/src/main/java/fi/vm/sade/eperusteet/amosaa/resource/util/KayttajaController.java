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

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jhyoty
 */
@RestController
@RequestMapping("/kayttaja")
@Api(value = "kayttaja")
@ApiIgnore
public class KayttajaController {

    @Autowired
    private KayttajanTietoService kayttajat;

    @RequestMapping(method = RequestMethod.GET)
    public KayttajanTietoDto get() {
        return kayttajat.haeKirjautaunutKayttaja();
    }

    @RequestMapping(value = "/{oid}", method = RequestMethod.POST)
    public KayttajaDto getOrSaveKayttaja(@PathVariable final String oid) {
        return kayttajat.saveKayttaja(oid);
    }

    @RequestMapping(value = "/tiedot", method = RequestMethod.GET)
    public KayttajaDto getKayttaja() {
        return kayttajat.haeKayttajanTiedot();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.POST)
    public ResponseEntity addSuosikki(@PathVariable final Long opsId) {
        kayttajat.addSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.DELETE)
    public ResponseEntity removeSuosikki(@PathVariable final Long opsId) {
        kayttajat.removeSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.koulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.POST)
    public ResponseEntity updateKoulutustoimijat(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.updateKoulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app))
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/organisaatiot", method = RequestMethod.GET)
    public Set<String> getOrganisaatiot(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.getUserOrganizations(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public EtusivuDto getKayttajanEtusivu(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.haeKayttajanEtusivu(PermissionEvaluator.RolePrefix.valueOf(app));
    }
}
