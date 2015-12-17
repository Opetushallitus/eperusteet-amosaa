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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.generic.rest.CachingRestClient;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 *
 * @author mikkom
 */
@Service
public class OrganisaatioServiceImpl implements OrganisaatioService {

    private static final String ORGANISAATIOT = "/rest/organisaatio/";
    private static final String HIERARKIA_HAKU = "v2/hierarkia/hae?";
    private static final String KUNTA_KRITEERI = "kunta=";
    private static final String STATUS_KRITEERI = "&aktiiviset=true&suunnitellut=true&lakkautetut=false";
    private static final String ORGANISAATIO_KRITEERI = "oidRestrictionList=";

    @Autowired
    private Client client;

    @Component
    public static class Client {
        @Autowired
        RestClientFactory restClientFactory;

        @Value("${cas.service.organisaatio-service:''}")
        private String serviceUrl;

        private static final Logger LOG = LoggerFactory.getLogger(Client.class);

        private final ObjectMapper mapper = new ObjectMapper();

        @PostConstruct
        public void init() {
        }

        @Cacheable("organisaatiot")
        public JsonNode getOrganisaatio(String organisaatioOid) {
            CachingRestClient crc = restClientFactory.getWithoutCas(serviceUrl);
            String url = serviceUrl + ORGANISAATIOT + organisaatioOid;
            try {
                return mapper.readTree(crc.getAsString(url));
            } catch (IOException ex) {
                throw new BusinessRuleViolationException("Organisaation tietojen hakeminen epäonnistui", ex);
            }
        }
    }

    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        return client.getOrganisaatio(organisaatioOid);
    }
}
