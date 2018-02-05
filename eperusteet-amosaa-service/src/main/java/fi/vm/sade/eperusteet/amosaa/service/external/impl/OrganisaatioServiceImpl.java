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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fi.vm.sade.eperusteet.amosaa.dto.OrganisaatioHierarkia;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.generic.rest.CachingRestClient;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author mikkom
 */
@Service
@Profile("default")
public class OrganisaatioServiceImpl implements OrganisaatioService {

    private static final String ORGANISAATIOT = "/rest/organisaatio/";

    @Autowired
    private Client client;

    private final ObjectMapper mapper = new ObjectMapper();

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
                return null;
            }
        }

//        @Cacheable("organisaatiohierarkia")
        public JsonNode getOrganisaatioPuu(String organisaatioOid) {
            CachingRestClient crc = restClientFactory.getWithoutCas(serviceUrl);
            String url = serviceUrl + ORGANISAATIOT + "v2/hierarkia/hae?aktiiviset=true&suunnitellut=true&lakkautetut=false&oid=" + organisaatioOid;
            try {
                return mapper.readTree(crc.getAsString(url));
            } catch (IOException ex) {
                return null;
            }
        }
    }

    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        return client.getOrganisaatio(organisaatioOid);
    }

    private void removeUnwanteds(ArrayNode parent) {
        Iterator<JsonNode> iterator = parent.iterator();
        while (iterator.hasNext()) {
            JsonNode child = iterator.next();

            if (child.hasNonNull("oppilaitostyyppi")) {
                String oppilaitostyyppi = child.get("oppilaitostyyppi").asText();
                // Poistetaan jos ei ole sallittu oppilaitostyyppi (ks. oppilaitostyyppi koodistopalvelusta)
                if(Stream.of(21, 22, 23, 24, 29, 93)
                        .map(olt -> "oppilaitostyyppi_" + olt)
                        .noneMatch(oppilaitostyyppi::startsWith)) {
                    iterator.remove();
                }
            }

            if (child.hasNonNull("children")) {
                ArrayNode children = (ArrayNode) child.get("children");
                removeUnwanteds(children);
            }
        }

    }

    @Override
    public OrganisaatioHierarkia getOrganisaatioPuu(String organisaatioOid) {
        JsonNode puu = client.getOrganisaatioPuu(organisaatioOid);
        try {
            if (puu.get("organisaatiot").size() == 0 || SecurityUtil.OPH_OID.equals(organisaatioOid)) {
                return null;
            }
            JsonNode organisaatiot = puu.get("organisaatiot").get(0);

            if (organisaatiot.hasNonNull("children")) {
                removeUnwanteds((ArrayNode) organisaatiot.get("children"));
            }

            return mapper.treeToValue(organisaatiot, OrganisaatioHierarkia.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

}
