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
package fi.vm.sade.eperusteet.amosaa.test;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mikkom
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/it-test-context.xml")
@ActiveProfiles(profiles = "test")
public class AbstractIntegrationTest {

    @Autowired
    protected KoulutustoimijaService koulutustoimijaService;

    @Autowired
    protected KayttajanTietoService kayttajanTietoService;

    protected List<KoulutustoimijaBaseDto> koulutustoimijat = new ArrayList<>();
    protected Kayttaja kayttaja = null;

    protected Long getKoulutustoimijaId() {
        return getKoulutustoimija().getId();
    }

    protected KoulutustoimijaBaseDto getKoulutustoimija() {
        return koulutustoimijat.get(0);
    }

    protected void useProfileTest1() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken("test1", "test"));
        SecurityContextHolder.setContext(ctx);
        kayttajanTietoService.updateKoulutustoimijat();
        this.koulutustoimijat = kayttajanTietoService.koulutustoimijat();
        kayttaja = kayttajanTietoService.getKayttaja();
    }

    protected void useProfileTest2() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken("test2", "test"));
        SecurityContextHolder.setContext(ctx);
        kayttajanTietoService.updateKoulutustoimijat();
        this.koulutustoimijat = kayttajanTietoService.koulutustoimijat();
        kayttaja = kayttajanTietoService.getKayttaja();
    }

    @Before
    public void useProfileTest() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
        SecurityContextHolder.setContext(ctx);
        kayttajanTietoService.updateKoulutustoimijat();
        this.koulutustoimijat = kayttajanTietoService.koulutustoimijat();
        kayttaja = kayttajanTietoService.getKayttaja();
    }
}
