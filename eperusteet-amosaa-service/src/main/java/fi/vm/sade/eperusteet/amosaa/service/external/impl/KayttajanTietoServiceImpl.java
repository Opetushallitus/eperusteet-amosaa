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
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KoulutustoimijaKayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import static fi.vm.sade.eperusteet.amosaa.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.generic.rest.CachingRestClient;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
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
    private DtoMapper mapper;

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
        @Value("${cas.service.authentication-service:''}")
        private String serviceUrl;

        private static final String KAYTTAJA_API = "/resources/henkilo/";
        private static final String OMAT_TIEDOT_API = "/resources/omattiedot/";
        private final ObjectMapper mapper = new ObjectMapper();

        @Cacheable("kayttajat")
        public KayttajanTietoDto hae(String oid) {
            try {
                CachingRestClient crc = restClientFactory.get(serviceUrl);
                String url = serviceUrl + (oid == null ? OMAT_TIEDOT_API : KAYTTAJA_API + oid);
                JsonNode json = mapper.readTree(crc.getAsString(url));
                return parsiKayttaja(json);
            } catch (Exception e) {
                KayttajanTietoDto result = new KayttajanTietoDto();
                result.setOidHenkilo(oid);
                return result;
            }
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
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        KayttajanTietoDto kayttajatieto = hae(getUserOid());
        if (kayttajatieto == null) { //"fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttajatieto = new KayttajanTietoDto();
            kayttajatieto.setOidHenkilo(getUserOid());
        }
        return kayttajatieto;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateKoulutustoimijat() {
        koulutustoimijaService.initKoulutustoimijat(getUserOrganizations());
        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public List<KoulutustoimijaBaseDto> koulutustoimijat() {
        Kayttaja kayttaja = getKayttaja();

        return koulutustoimijaService.getKoulutustoimijat(getUserOrganizations()).stream()
                .filter(org -> org != null)
                .map(ktDto -> {
                    Koulutustoimija kt = koulutustoimijaRepository.findOne(ktDto.getId());
                    if (ktkayttajaRepository.findOneByKoulutustoimijaAndKayttaja(kt, kayttaja) == null) {
                        KoulutustoimijaKayttaja ktk = new KoulutustoimijaKayttaja();
                        ktk.setKayttaja(kayttaja);
                        ktk.setKoulutustoimija(kt);
                        ktkayttajaRepository.save(ktk);
                    }
                    return ktDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<KayttajaDto> getKayttajat(Long kOid) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(kOid);
        return ktkayttajaRepository.findAllByKoulutustoimija(kt).stream()
                .map(kayttaja -> kayttajaRepository.findOneByOid(kayttaja.getKayttaja().getOid()))
                .map(kayttaja -> mapper.map(kayttaja, KayttajaDto.class))
                .collect(Collectors.toList());
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
