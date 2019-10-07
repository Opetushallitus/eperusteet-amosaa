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

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author nkala
 */
@Service
@Profile(value = "default")
public class KoodistoClientImpl implements KoodistoClient {
    @Value("${koodisto.service.url:https://virkailija.opintopolku.fi/koodisto-service}")
    private String koodistoServiceUrl;

    private static final String KOODISTO_API = "/rest/json/";
    private static final String YLARELAATIO = "relaatio/sisaltyy-ylakoodit/";
    private static final String ALARELAATIO = "relaatio/sisaltyy-alakoodit/";

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private HttpEntity httpEntity;

    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceUrl + KOODISTO_API + koodisto + "/koodi/";

        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

    @Override
    @Cacheable("koodistokoodit")
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceUrl + KOODISTO_API + koodisto + "/koodi/" + koodi;
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
        String url = koodistoServiceUrl + KOODISTO_API + ALARELAATIO + koodi;
        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

    @Override
    @Cacheable(value = "koodistot", key = "'ylarelaatio:'+#p0")
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        RestTemplate restTemplate = new RestTemplate();
        String url = koodistoServiceUrl + KOODISTO_API + YLARELAATIO + koodi;
        ResponseEntity<KoodistoKoodiDto[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoDtot = mapper.mapAsList(Arrays.asList(response.getBody()), KoodistoKoodiDto.class);
        return koodistoDtot;
    }

}
