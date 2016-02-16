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
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanKoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.generic.rest.CachingRestClient;
import java.util.List;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static fi.vm.sade.eperusteet.amosaa.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import java.security.Principal;
import java.util.ArrayList;

/**
 * @author mikkom
 */
@Service
public class KayttajanTietoServiceImpl implements KayttajanTietoService {
    @Autowired
    private KayttajaClient client;

    @Override
    public KayttajanTietoDto hae(String oid) {
        return client.hae(oid);
    }

    @Override
    @Async
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        Principal ap = SecurityUtil.getAuthenticatedPrincipal();
        KayttajanTietoDto kayttaja = hae(ap.getName());
        if (kayttaja == null) { //"fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttaja = new KayttajanTietoDto();
            kayttaja.setOidHenkilo(ap.getName());
        }
        return kayttaja;
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
                return null;
            }
        }
    }

    @Override
    public List<KayttajanKoulutustoimijaDto> koulutustoimijat() {
        ArrayList<KayttajanKoulutustoimijaDto> result = new ArrayList<>();
        KayttajanKoulutustoimijaDto kkt = new KayttajanKoulutustoimijaDto();
        kkt.setKoulutustoimija("1.2.246.562.10.20516711478");
        result.add(kkt);
        return result;
    }

}
