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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;

/**
 * @author mikkom
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/it-test-context.xml")
@ActiveProfiles(profiles = "test")
public abstract class AbstractIntegrationTest {
    
    static public String KP1 = "kp1";
    static public String KP2 = "kp2";
    
    static public String oidOph = "1.2.246.562.10.00000000001";
    static public String oidKp1 = "1.2.246.562.10.54645809036";
    static public String oidKp2 = "1.2.246.562.10.2013120512391252668625";
    static public String oidKp3 = "1.2.246.562.10.2013120513110198396408";
    static public String oidTmpr = "1.2.246.562.10.79499343246";

    @Autowired
    protected OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    protected KoulutustoimijaService koulutustoimijaService;

    @Autowired
    protected KayttajanTietoService kayttajanTietoService;
    
    @Autowired
    protected OpetussuunnitelmaRepository opetussuunnitelmaRepository;  
    
    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;
    
    protected List<KoulutustoimijaBaseDto> koulutustoimijat = new ArrayList<>();
    protected KoulutustoimijaBaseDto toimija = null;
    protected Kayttaja kayttaja = null;

    protected Long getKoulutustoimijaId() {
        return getKoulutustoimija().getId();
    }

    protected KoulutustoimijaBaseDto getKoulutustoimija() {
        return toimija;
    }

    protected KayttajaoikeusDto updateUserOikeus(Long ktId, Long opsId, KayttajaoikeusTyyppi oikeus, String kayttajaOid) {
        KayttajaDto user = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId()).stream()
                .filter(kayttaja -> kayttaja.getOid().equals(kayttajaOid))
                .findFirst()
                .get();
        KayttajaoikeusDto result = new KayttajaoikeusDto();
        result.setOikeus(oikeus);
        result.setKayttaja(Reference.of(user.getId()));
        result = opetussuunnitelmaService.updateOikeus(getKoulutustoimijaId(), opsId, user.getId(), result);
        return result;
    }


    protected OpetussuunnitelmaBaseDto createOpetussuunnitelma() {
        return createOpetussuunnitelma((ops) -> {});
    }
    
    protected Opetussuunnitelma createOpetussuunnitelmaJulkaistu() {	
    	OpetussuunnitelmaBaseDto dto = createOpetussuunnitelma();	
    	Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(dto.getId());
    	ops.setTila(Tila.JULKAISTU);
    	return opetussuunnitelmaRepository.save(ops);
    }
    
    protected Opetussuunnitelma updateOpetussuunnitelmaJulkaisukielet(Opetussuunnitelma opetussuunnitelma, Set<Kieli> kielet) {
    	opetussuunnitelma.setJulkaisukielet(kielet);
    	return opetussuunnitelmaRepository.save(opetussuunnitelma);
    }
        
    protected void updateKoulutustoimijaLokalisointiNimet(Map<Kieli, String> tekstit) {
    	Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(toimija.getId());
    	koulutustoimija.setNimi(LokalisoituTeksti.of(tekstit));
    	koulutustoimijaRepository.save(koulutustoimija);
    }
    
    protected OpetussuunnitelmaBaseDto createOpetussuunnitelma(Consumer<OpetussuunnitelmaDto> opsfn) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setPerusteDiaarinumero("9/011/2008");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        HashMap<String, String> nimi = new HashMap<>();
        nimi.put("fi", "auto");
        ops.setNimi(new LokalisoituTekstiDto(nimi));
        opsfn.accept(ops);
        return opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
    }

    protected OpetussuunnitelmaBaseDto createPohja() {
        return createPohja((ops) -> {});
    }

    protected OpetussuunnitelmaBaseDto createPohja(Consumer<OpetussuunnitelmaDto> opsfn) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setTyyppi(OpsTyyppi.POHJA);
        HashMap<String, String> nimi = new HashMap<>();
        nimi.put("fi", "auto");
        ops.setNimi(new LokalisoituTekstiDto(nimi));
        ops.setJulkaisukielet(new HashSet<Kieli>() {{ add(Kieli.FI); }});
        opsfn.accept(ops);
        OpetussuunnitelmaBaseDto pohja = opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
        return pohja;
    }

    protected SisaltoViiteDto.Matala createSisalto() {
        return createSisalto((ops) -> {});
    }

    protected SisaltoViiteDto.Matala createSisalto(Consumer<SisaltoViiteDto> sisaltoFn) {
        SisaltoViiteDto.Matala result = new SisaltoViiteDto.Matala();
        result.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        sisaltoFn.accept(result);
        return result;
    }

    @Before
    public void useProfileTest() {
        useProfileKP2();
    }

    private void updateProfile(String username) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(username, "test"));
        SecurityContextHolder.setContext(ctx);
        kayttajanTietoService.updateKoulutustoimijat();
        this.koulutustoimijat = kayttajanTietoService.koulutustoimijat();
        this.toimija = this.koulutustoimijat.get(0);
        kayttaja = kayttajanTietoService.getKayttaja();
    }

    protected void useProfileKP1() {
        updateProfile("kp1");
    }

    protected void useProfileKP2() {
        updateProfile("kp2");
    }

    protected void useProfileKP2user2() {
        updateProfile("kp2user2");
    }

    protected void useProfileKP3() {
        updateProfile("kp3");
    }

    protected void useProfileOPH() {
        updateProfile("oph");
    }

    protected void useProfileTmpr() {
        updateProfile("tmpr");
    }

    protected void regAllProfiles() {
        useProfileOPH();
        useProfileKP1();
        useProfileKP2();
        useProfileKP2user2();
        useProfileKP3();
        useProfileTmpr();
    }
}
