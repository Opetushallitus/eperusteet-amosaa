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

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/api/kayttaja")
@Api(value = "kayttajaoikeudet")
@InternalApi
public class KayttajaoikeusController {
    @Autowired
    private KayttajaoikeusService service;

    @RequestMapping(value = "/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getTyoryhmat() {
        return service.getKayttooikeudet();
    }

    @RequestMapping(value = "/organisaatiooikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> getOikeudet(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return service.getOrganisaatiooikeudet(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/koulutustoimijaoikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<KoulutustoimijaBaseDto>>> getKoulutustoimijaOikeudet(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return service.getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix.valueOf(app));
    }
}
