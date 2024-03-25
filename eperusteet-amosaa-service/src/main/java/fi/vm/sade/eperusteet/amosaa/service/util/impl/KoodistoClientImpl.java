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

package fi.vm.sade.eperusteet.amosaa.service.util.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author nkala
 */
@Lazy
@Service
@Profile(value = "default")
@Slf4j
public class KoodistoClientImpl implements KoodistoClient {
    @Value("${koodisto.service.url:https://virkailija.opintopolku.fi/koodisto-service}")
    private String koodistoServiceUrl;

    @Value("${koodisto.service.internal.url:${koodistoServiceUrl: ''}}")
    private String koodistoServiceInternalUrl;

    private static final String KOODISTO_API = "/rest/json/";
    private static final String YLARELAATIO = "relaatio/sisaltyy-ylakoodit/";
    private static final String ALARELAATIO = "relaatio/sisaltyy-alakoodit/";
    private static final String CODEELEMENT = "/rest/codeelement";

    private static final int KOODISTO_TEKSTI_MAX_LENGTH = 512;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private HttpEntity httpEntity;

    @Autowired
    private RestClientFactory restClientFactory;

    @Autowired
    KoodistoClient self; // for cacheable

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceInternalUrl + KOODISTO_API + koodisto + "/koodi/";

        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

    @Override
    @Cacheable("koodistokoodit")
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceInternalUrl + KOODISTO_API + koodisto + "/koodi/" + koodi;
        try {
            ResponseEntity<KoodistoKoodiDto> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            return null;
        }
    }

    @Override
    public KoodistoKoodiDto getByUri(String uri) {
        String[] splitted = uri.split("_");
        if (splitted.length < 2) {
            return null;
        } else if (splitted.length == 2) {
            return get(splitted[0], uri);
        } else if (splitted[0].startsWith("paikallinen_tutkinnonosa")) {
            return null; // FIXME
        } else {
            return null;
        }
    }

    @Override
    @Cacheable("koodistohaku")
    public List<KoodistoKoodiDto> queryByKoodi(String koodisto, String query) {
        return getAll(koodisto).stream()
                .filter(k -> k.getKoodiArvo().contains(query) || Arrays.asList(k.getMetadata()).stream()
                        .anyMatch(meta -> meta.getNimi().contains(query)))
                .collect(Collectors.toList());
    }

    @Override
    public List<KoodistoKoodiDto> filterBy(String koodisto, String koodi) {
        List<KoodistoKoodiDto> filter = getAll(koodisto);
        List<KoodistoKoodiDto> tulos = new ArrayList<>();

        for (KoodistoKoodiDto x : filter) {
            Boolean nimessa = false;

            for (KoodistoMetadataDto y : x.getMetadata()) {
                if (y.getNimi().toLowerCase().contains(koodi.toLowerCase())) {
                    nimessa = true;
                    break;
                }
            }

            if (x.getKoodiUri().contains(koodi) || nimessa) {
                tulos.add(x);
            }
        }
        return tulos;
    }

    @Override
    @Cacheable(value = "koodistot", key = "'alarelaatio:'+#p0")
    public List<KoodistoKoodiDto> getAlarelaatio(String koodi) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceInternalUrl + KOODISTO_API + ALARELAATIO + koodi;
        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

    @Override
    @Cacheable(value = "koodistot", key = "'ylarelaatio:'+#p0")
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceInternalUrl + KOODISTO_API + YLARELAATIO + koodi;
        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

    @Override
    public KoodistoKoodiDto addKoodi(KoodistoKoodiDto koodi) {
        OphHttpClient client = restClientFactory.get(koodistoServiceUrl, true);

        String url = koodistoServiceInternalUrl
                + CODEELEMENT + "/"
                + koodi.getKoodisto().getKoodistoUri();
        try {
            String dataStr = objectMapper.writeValueAsString(koodi);
            OphHttpRequest request = OphHttpRequest.Builder
                    .post(url)
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .setEntity(new OphHttpEntity.Builder()
                            .content(dataStr)
                            .contentType(ContentType.APPLICATION_JSON)
                            .build())
                    .build();

            return client.<KoodistoKoodiDto>execute(request)
                    .handleErrorStatus(SC_UNAUTHORIZED, SC_FORBIDDEN, SC_METHOD_NOT_ALLOWED, SC_BAD_REQUEST, SC_INTERNAL_SERVER_ERROR)
                    .with(res -> {
                        return Optional.empty();
                    })
                    .expectedStatus(SC_OK, SC_CREATED)
                    .mapWith(text -> {
                        try {
                            return objectMapper.readValue(text, KoodistoKoodiDto.class);
                        } catch (IOException e) {
                            throw new BusinessRuleViolationException("koodin-parsinta-epaonnistui");
                        }
                    })
                    .orElse(null);
        } catch (JsonProcessingException e) {
            throw new BusinessRuleViolationException("koodin-lisays-epaonnistui");
        }
    }

    @Override
    public KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi) {
        long seuraavaKoodi = nextKoodiId(koodistonimi);
        return addKoodiNimella(koodistonimi, koodinimi, seuraavaKoodi);
    }

    public static boolean validoiKooditettava(LokalisoituTekstiDto koodinimi) {
        return koodinimi != null
                && koodinimi.getTekstit() != null
                && koodinimi.getTekstit().values().stream().noneMatch(teksti -> teksti != null && (teksti.length() == 0 || teksti.length() > KOODISTO_TEKSTI_MAX_LENGTH));
    }

    @Override
    public KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi, long seuraavaKoodi) {

        if (!KoodistoClientImpl.validoiKooditettava(koodinimi)) {
            throw new BusinessRuleViolationException("koodi-arvo-liian-pitka");
        }

        KoodistoKoodiDto uusiKoodi = KoodistoKoodiDto.builder()
                .koodiArvo(Long.toString(seuraavaKoodi))
                .koodiUri(koodistonimi + "_" + seuraavaKoodi)
                .koodisto(KoodistoDto.of(koodistonimi))
                .voimassaAlkuPvm(new Date())
                .metadata(koodinimi.getTekstit().entrySet().stream()
                        .map((k) -> KoodistoMetadataDto.of(k.getValue(), k.getKey().toString().toUpperCase(), k.getValue()))
                        .toArray(KoodistoMetadataDto[]::new))
                .build();
        KoodistoKoodiDto lisattyKoodi = addKoodi(uusiKoodi);
        if (lisattyKoodi == null
                || lisattyKoodi.getKoodisto() == null
                || lisattyKoodi.getKoodisto().getKoodistoUri() == null
                || lisattyKoodi.getKoodiUri() == null) {
            log.error("Koodin lisääminen epäonnistui {} {}", uusiKoodi, lisattyKoodi);
            return null;
        }

        return lisattyKoodi;
    }

    @Override
    public long nextKoodiId(String koodistonimi) {
        return nextKoodiId(koodistonimi, 1).stream().findFirst().get();
    }

    @Override
    public Collection<Long> nextKoodiId(String koodistonimi, int count) {
        List<KoodistoKoodiDto> koodit = self.getAll(koodistonimi);
        if (koodit.size() == 0) {
            koodit = Collections.singletonList(KoodistoKoodiDto.builder().koodiArvo("999").build());
        }

        List<Long> ids = new ArrayList<>();
        List<Long> currentIds = koodit.stream().map(k -> Long.parseLong(k.getKoodiArvo())).collect(Collectors.toList());
        Long max = currentIds.stream().mapToLong(Long::longValue).max().getAsLong();
        Long min = currentIds.stream().mapToLong(Long::longValue).min().getAsLong();

        for (Long ind = min; ind <= max + count && ids.size() < count; ind++) {
            if (!currentIds.contains(ind)) {
                ids.add(ind);
            }
        }

        return ids;
    }

}
