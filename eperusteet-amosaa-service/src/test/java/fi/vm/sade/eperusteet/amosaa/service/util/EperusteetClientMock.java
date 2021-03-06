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
package fi.vm.sade.eperusteet.amosaa.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.PalauteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nkala
 */
@Service
@Profile("test")
@SuppressWarnings("TransactionalAnnotations")
@Transactional
public class EperusteetClientMock implements EperusteetClient {

    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${test.perusteJsonFile:/perusteet/testiperuste.json}")
    private String perusteJsonFile;

    @PostConstruct
    protected void init() {
        objectMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    public static final String DIAARINUMERO = "mock-diaarinumero";

    @Override
    public ArviointiasteikkoDto getArviointiasteikko(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getPeruste(String diaariNumero, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public PerusteDto getYleinenPohja() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getYleinenPohjaSisalto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Autowired
    private DtoMapper mapper;

    private JsonNode getMockPeruste() {
        try {
            File file = applicationContext.getResource(perusteJsonFile).getFile();
            JsonNode peruste = objectMapper.readTree(file);
            return peruste;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessRuleViolationException("tiedostoa ei löytynyt");
        }
    }

    @Override
    public String getPerusteData(Long id) {
        try {
            JsonNode node = getMockPeruste();
            Object perusteObj = objectMapper.treeToValue(node, Object.class);
            String json = objectMapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        PerusteDto peruste = new PerusteDto();
        peruste.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(peruste);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        return null;
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        return null;
    }

    @Override
    public <T> T getPeruste(Long id, Class<T> type) {
        try {
            JsonNode perusteJson = getMockPeruste();
            T result = objectMapper.treeToValue(perusteJson, type);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessRuleViolationException("tiedostoa ei löytynyt");
        }
    }

    @Override
    public JsonNode findFromPerusteet(Map<String, String> query) {
        return null;
    }

    @Override
    public PalauteDto lahetaPalaute(PalauteDto palaute) throws JsonProcessingException {
        return null;
    }

    @Override
    public <T> T getPerusteOrNull(Long id, Class<T> type) {
        return null;
    }
}
