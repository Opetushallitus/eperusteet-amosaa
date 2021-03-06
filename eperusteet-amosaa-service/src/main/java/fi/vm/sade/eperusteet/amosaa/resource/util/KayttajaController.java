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
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat() {
        return kayttajat.koulutustoimijat();
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.POST)
    public ResponseEntity updateKoulutustoimijat() {
        return kayttajat.updateKoulutustoimijat()
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/organisaatiot", method = RequestMethod.GET)
    public Set<String> getOrganisaatiot() {
        return kayttajat.getUserOrganizations();
    }

    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public EtusivuDto getKayttajanEtusivu() {
        return kayttajat.haeKayttajanEtusivu();
    }
}
