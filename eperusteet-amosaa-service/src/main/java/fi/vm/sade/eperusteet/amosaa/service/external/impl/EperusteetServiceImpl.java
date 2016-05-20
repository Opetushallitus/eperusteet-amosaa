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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import java.io.IOException;
import java.util.*;
import static java.util.Collections.singletonList;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author nkala
 *
 * FIXME: Refaktoroi turhat pois
 */
@Service
@Profile(value = "default")
@SuppressWarnings("TransactionalAnnotations")
@Transactional
public class EperusteetServiceImpl implements EperusteetService {
    private static final Logger logger = LoggerFactory.getLogger(EperusteetServiceImpl.class);

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    private JsonMapper jsonMapper;

    private RestTemplate client;

    private ObjectMapper mapper;

    @Override
    public JsonNode getTutkinnonOsat(Long id) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            return node.get("tutkinnonOsat");
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public JsonNode getRakenne(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JsonNode getTutkinnonOsa(Long id, Long tosaId) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            for (JsonNode tosa : node.get("tutkinnonOsat")) {
                if (tosaId.equals(tosa.get("id").asLong())) {
                    return tosa;
                }
            }
            return null;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public <T> T getPerusteSisalto(Long cperusteId, Class<T> type) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(cperusteId);
        return getPerusteSisalto(cperuste, type);
    }

    @Override
    public <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type) {
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            T peruste = mapper.treeToValue(node, type);
            return peruste;
        } catch (IOException ex) {
            logger.debug("Perusteen parsinta epäonnistui", ex);
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public String getPerusteData(Long id) {
        try {
            JsonNode node = client.getForObject(eperusteetServiceUrl + "/api/perusteet/" + String.valueOf(id) + "/kaikki", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public <T> T getPeruste(String diaarinumero, Class<T> type) {
        try {
            JsonNode node = client.getForObject(eperusteetServiceUrl + "/api/perusteet/diaari?diaarinumero=" + diaarinumero, JsonNode.class);
            T peruste = mapper.treeToValue(node, type);
            return peruste;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @PostConstruct
    protected void init() {
        client = new RestTemplate(singletonList(jsonMapper.messageConverter().orElseThrow(IllegalStateException::new)));
        mapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    public PerusteDto getYleinenPohja() {
        try {
            JsonNode node = client.getForObject(eperusteetServiceUrl + "/api/perusteet/amosaapohja", JsonNode.class);
            PerusteDto peruste = mapper.treeToValue(node, PerusteDto.class);
            return peruste;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("pohjaa-ei-loytynyt");
        }
    }

    @Override
    public String getYleinenPohjaSisalto() {
        try {
            JsonNode node = client.getForObject(eperusteetServiceUrl + "/api/perusteet/amosaapohja", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public List<PerusteInfoDto> findPerusteet() {
        return new ArrayList<>();
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        return new ArrayList<>();
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        JsonNode tiedotteet = client.getForObject(eperusteetServiceUrl + "/api/tiedotteet" + params, JsonNode.class);
        return tiedotteet;
    }
}
