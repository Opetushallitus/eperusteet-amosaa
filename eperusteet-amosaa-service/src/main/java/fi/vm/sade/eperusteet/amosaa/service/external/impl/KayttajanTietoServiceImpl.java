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
package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KoulutustoimijaKayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KoulutustoimijaKayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;

import static fi.vm.sade.eperusteet.amosaa.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;


import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nkala
 */
@Service
@Transactional
public class KayttajanTietoServiceImpl implements KayttajanTietoService {
    @Autowired
    private KayttajaClient client;

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private KoulutustoimijaKayttajaRepository ktkayttajaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private DtoMapper mapper;
    
    @Autowired
    private KayttooikeusService kayttooikeusService;

    @Override
    public KayttajaDto haeKayttajanTiedot() {
        Kayttaja kayttaja = getKayttaja();
        return mapper.map(kayttaja, KayttajaDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeSuosikki(Long opsId) {
        Kayttaja kayttaja = getKayttaja();
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-olemassa");
        }
        kayttaja.getSuosikit().remove(ops);
    }

    @Override
    @Transactional(readOnly = false)
    public void addSuosikki(Long opsId) {
        Kayttaja kayttaja = getKayttaja();
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-olemassa");
        }
        kayttaja.getSuosikit().add(ops);
    }

    @Override
    public KayttajanTietoDto hae(String oid) {
        return client.hae(oid);
    }

    @Override
    public KayttajanTietoDto hae(Long id) {
        return client.hae(kayttajaRepository.findOne(id).getOid());
    }

    @Override
    @Async
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Component
    public static class KayttajaClient {

        @Autowired
        private RestClientFactory restClientFactory;

        @Value("${cas.service.oppijanumerorekisteri-service:''}")
        private String onrServiceUrl;

        private static final String HENKILO_API = "/henkilo/";
        private final ObjectMapper mapper = new ObjectMapper();

        @Cacheable("kayttajat")
        public KayttajanTietoDto hae(String oid) {
            OphHttpClient client = restClientFactory.get(onrServiceUrl, true);
            String url = onrServiceUrl + HENKILO_API + oid;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<KayttajanTietoDto>execute(request)
                    .handleErrorStatus(SC_UNAUTHORIZED, SC_FORBIDDEN)
                    .with((res) -> Optional.of(new KayttajanTietoDto(oid)))
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            JsonNode json = mapper.readTree(text);
                            return parsiKayttaja(json);
                        } catch (IOException e) {
                            return new KayttajanTietoDto(oid);
                        }
                    })
                    .orElse(new KayttajanTietoDto(oid));
        }
    }

    @Override
    public String getUserOid() {
        return SecurityUtil.getAuthenticatedPrincipal().getName();
    }

    @Override
    @Cacheable(value = "kayttajatiedot", unless = "#result == null")
    public KayttajanTietoDto haeNimi(Long id) {
        Kayttaja kayttaja = kayttajaRepository.findOne(id);
        return hae(kayttaja.getOid());
    }

    @Override
    public Kayttaja getKayttaja() {
        String oid = SecurityUtil.getAuthenticatedPrincipal().getName();
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja == null) {
            Kayttaja uusi = new Kayttaja();
            uusi.setOid(oid);
            kayttaja = kayttajaRepository.save(uusi);
        }
        return kayttaja;
    }
    
    @Override
    public KayttajaDto saveKayttajaToKoulutustoimija(String oid, Long koulutustoimijaId) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja == null) {
            Kayttaja uusi = new Kayttaja();
            uusi.setOid(oid);
            kayttaja = kayttajaRepository.save(uusi);
        }

        Koulutustoimija kt = koulutustoimijaRepository.findOne(koulutustoimijaId);
        if (ktkayttajaRepository.findOneByKoulutustoimijaAndKayttaja(kt, kayttaja) == null) {
            KoulutustoimijaKayttaja ktk = new KoulutustoimijaKayttaja();
            ktk.setKayttaja(kayttaja);
            ktk.setKoulutustoimija(kt);
            ktkayttajaRepository.save(ktk);
        }

        return mapper.map(kayttaja, KayttajaDto.class);
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        KayttajanTietoDto kayttajatieto = hae(getUserOid());
        if (kayttajatieto == null) {
            // "fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttajatieto = new KayttajanTietoDto(getUserOid());
        }
        return kayttajatieto;
    }

    @Override
    public boolean updateKoulutustoimijat() {
        koulutustoimijaService.initKoulutustoimijat(getUserOrganizations());
        return true;
    }

    @Override
    public List<KoulutustoimijaBaseDto> koulutustoimijat() {
        Kayttaja kayttaja = getKayttaja();

        return koulutustoimijaService.getKoulutustoimijat(getUserOrganizations()).stream()
                .filter(ktDto -> ktDto != null && ktDto.getId() != null)
                .peek(ktDto -> {
                    Koulutustoimija kt = koulutustoimijaRepository.findOne(ktDto.getId());
                    if (ktkayttajaRepository.findOneByKoulutustoimijaAndKayttaja(kt, kayttaja) == null) {
                        KoulutustoimijaKayttaja ktk = new KoulutustoimijaKayttaja();
                        ktk.setKayttaja(kayttaja);
                        ktk.setKoulutustoimija(kt);
                        ktkayttajaRepository.save(ktk);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<KayttajaDto> getKayttajat(Koulutustoimija kt) {
        if (kt.isOph()) {
            return new ArrayList<>();
        }
        else {
            return ktkayttajaRepository.findAllByKoulutustoimija(kt).stream()
                    .map(kayttaja -> kayttajaRepository.findOneByOid(kayttaja.getKayttaja().getOid()))
                    .map(kayttaja -> mapper.map(kayttaja, KayttajaDto.class))
                    .peek(kayttaja -> kayttaja.setKoulutustoimija(kt.getId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<KayttajaDto> getKayttajat(Long kOid) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(kOid);
        return getKayttajat(kt);
    }

    @Override
    public List<KayttajaDto> getKaikkiKayttajat(Long kOid) {
        Map<String, KayttajaDto> result = new HashMap<>();
        Koulutustoimija self = koulutustoimijaRepository.findOne(kOid);
        List<KoulutustoimijaYstavaDto> ystavaorganisaatiot = koulutustoimijaService.getOmatYstavat(self.getId());

        if (!self.isOph()) {
            result.putAll(getOrganisaatioVirkailijatAsKayttajat(Collections.singletonList(self.getOrganisaatio())));
            result.putAll(getOrganisaatioVirkailijatAsKayttajat(ystavaorganisaatiot.stream()
                    .map(KoulutustoimijaYstavaDto::getOrganisaatio).collect(Collectors.toList())));
        }

        List<String> oids = result.values().stream().map(KayttajaDto::getOid).collect(Collectors.toList());

        result.putAll(ystavaorganisaatiot.stream()
                .map(KoulutustoimijaYstavaDto::getId)
                .map(koulutustoimijaRepository::getOne)
                .map(this::getKayttajat)
                .flatMap(Collection::stream)
                .filter(kayttaja -> oids.contains(kayttaja.getOid()))
                .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja)));

        result.putAll(getKayttajat(kOid).stream()
                .filter(kayttaja -> oids.contains(kayttaja.getOid()))
                .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja))
        );

        return new ArrayList<>(result.values().stream()
                .collect(Collectors.toMap(
                        KayttajaBaseDto::getOid,
                        kayttaja -> kayttaja,
                        (a, b) -> kOid.equals(b.getKoulutustoimija()) ? b : a))
                .values());
    }

    private Map<String, KayttajaDto> getOrganisaatioVirkailijatAsKayttajat(List<String> organisaatioOids) {

        if(organisaatioOids.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> organisaatioKtIdMap = koulutustoimijaRepository.findByOrganisaatioIn(organisaatioOids).stream().collect(
                (Collectors.toMap(Koulutustoimija::getOrganisaatio, Koulutustoimija::getId)));

        return organisaatioOids.stream()
            .flatMap(organisaatioOid -> kayttooikeusService.getOrganisaatioVirkailijat(organisaatioOid).stream()
                    .map(kayttooikeusKayttajaDto -> KayttajaDto.of(kayttooikeusKayttajaDto, organisaatioKtIdMap.get(organisaatioOid))
                    ))
                .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja));
    }
    
    @Override
    public Set<String> getUserOrganizations() {
        return SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
    }

    @Override
    public KayttajanTietoDto getKayttaja(Long koulutustoimijaId, String oid) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja != null) {
            return client.hae(oid);
        }
        return null;
    }

    @Override
    public KayttajanTietoDto getKayttaja(Long koulutustoimijaId, Long id) {
        // FIXME
//        List<KayttajaoikeusTyyppi> oikeudet = kayttajaoikeusRepository.findKoulutustoimijaOikeus(koulutustoimijaId, kayttajaRepository.findOneByOid(oid).getId());
//        if (oikeudet.isEmpty()) {
//            throw new BusinessRuleViolationException("kayttajan-pitaa-kuulua-koulutustoimijaan");
//        }
        return hae(id);
    }

}
