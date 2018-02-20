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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaSuoritustapaDto;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetServiceClient;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.generic.rest.CachingRestClient;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nkala
 * <p>
 * FIXME: Refaktoroi turhat pois
 */
@Service
@Transactional
public class EperusteetServiceImpl implements EperusteetService {
    private static final Logger logger = LoggerFactory.getLogger(EperusteetServiceImpl.class);

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    RestClientFactory restClientFactory;

    @Autowired
    EperusteetServiceClient eperusteetServiceClient;

    private CachingRestClient client;

    private ObjectMapper mapper;

    @Autowired
    private DtoMapper dtoMapper;

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
    public CachedPerusteBaseDto getCachedPeruste(PerusteDto peruste) {
        CachedPeruste cperuste = cachedPerusteRepository.findOneByPerusteIdAndLuotu(peruste.getId(), peruste.getGlobalVersion().getAikaleima());
        if (cperuste == null) {
            cperuste = new CachedPeruste();
            if (peruste.getNimi() != null) {
                cperuste.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            }

            cperuste.setDiaarinumero(peruste.getDiaarinumero());
            cperuste.setPerusteId(peruste.getId());
            cperuste.setLuotu(peruste.getGlobalVersion().getAikaleima());
            cperuste.setPeruste(eperusteetServiceClient.getPerusteData(peruste.getId()));
            cperuste = cachedPerusteRepository.save(cperuste);
        }
        return dtoMapper.map(cperuste, CachedPerusteBaseDto.class);
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
            logger.error("Perusteen parsinta epäonnistui", ex);
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet() {
        HashSet<KoulutusTyyppi> koulutustyypit = new HashSet<>();
        koulutustyypit.add(KoulutusTyyppi.AMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.ERIKOISAMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.PERUSTUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.VALMA);
        koulutustyypit.add(KoulutusTyyppi.TELMA);
        return eperusteetServiceClient.findPerusteet(koulutustyypit);
    }

    @Override
    public <T> T getPerusteSisaltoByPerusteId(Long perusteId, Class<T> type) {
        try {
            String perusteData = eperusteetServiceClient.getPerusteData(perusteId);
            JsonNode node = mapper.readTree(perusteData);
            return mapper.treeToValue(node, type);
        } catch (IOException ex) {
            logger.error("Perusteen parsinta epäonnistui", ex);
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }
}
