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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaSuoritustapaDto;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.generic.rest.CachingRestClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    RestClientFactory restClientFactory;

    private CachingRestClient client;

    private ObjectMapper mapper;


    @PostConstruct
    protected void init() {
        client = restClientFactory.get(eperusteetServiceUrl, false);
        mapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

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
    public List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat) {
        List<TutkinnonOsaSuoritustapaDto> result = new ArrayList<>();
        for (JsonNode tosaJson : tutkinnonosat) {
            try {
                result.add(mapper.treeToValue(tosaJson, TutkinnonOsaSuoritustapaDto.class));
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(EperusteetServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new BusinessRuleViolationException("tutkinnon-osan-muuttaminen-epaonnistui");
            }
        }
        return result;
    }

    @Override
    public JsonNode getSuoritustavat(Long id) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            return node.get("suoritustavat");
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
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
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        return null;
    }

    @Override
    public JsonNode getSuoritustapa(Long id, String tyyppi) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            for (JsonNode suoritustapa : node.get("suoritustavat")) {
                if (suoritustapa.get("suoritustapakoodi").asText().equals(tyyppi)) {
                    return suoritustapa;
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        throw new BusinessRuleViolationException("suoritustapaa-ei-loytynyt");
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
            JsonNode node = commonGet("/api/perusteet/" + String.valueOf(id) + "/kaikki", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public <T> T getPeruste(Long id, Class<T> type) {
        T peruste = commonGet("/api/perusteet/" + id.toString() + "", type);
        return peruste;
    }

    @Override
    public <T> T getPeruste(String diaarinumero, Class<T> type) {
        T peruste = commonGet("/api/perusteet/diaari?diaarinumero=" + diaarinumero, type);
        return peruste;
    }

    @Override
    public PerusteDto getYleinenPohja() {
        PerusteDto peruste = commonGet("/api/perusteet/amosaapohja", PerusteDto.class);
        return peruste;
    }

    @Override
    public String getYleinenPohjaSisalto() {
        try {
            JsonNode node = commonGet("/api/perusteet/amosaapohja", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    private <T> T commonGet(String endpoint, Class<T> type) {
        try {
            InputStream stream = client.get(eperusteetServiceUrl + endpoint);
            T node = mapper.readValue(stream, type);
            return node;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("haku-epaonnistui");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        List<PerusteDto> perusteet = new ArrayList<>();
        JsonNode node = commonGet("/api/perusteet/amosaaops", JsonNode.class);
        for (JsonNode perusteJson : node) {
            try {
                PerusteDto peruste = mapper.treeToValue(perusteJson, PerusteDto.class);
                perusteet.add(peruste);
            }
            catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }

        return perusteet.stream()
                .filter(peruste -> tyypit.contains(peruste.getKoulutustyyppi()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PerusteDto> findPerusteet() {
        HashSet<KoulutusTyyppi> koulutustyypit = new HashSet<>();
        koulutustyypit.add(KoulutusTyyppi.AMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.ERIKOISAMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.PERUSTUTKINTO);
        return findPerusteet(koulutustyypit);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        JsonNode tiedotteet = commonGet(eperusteetServiceUrl + "/api/tiedotteet" + params, JsonNode.class);
        return tiedotteet;
    }
}
