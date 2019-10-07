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

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.repository.ops.ArviointiasteikkoRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.ArviointiasteikkoService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author isaul
 */
@Service
public class ArviointiasteikkoServiceImpl implements ArviointiasteikkoService {
    private static final Logger logger = LoggerFactory.getLogger(ArviointiasteikkoService.class);

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private ArviointiasteikkoRepository repository;

    @Autowired
    private HttpEntity httpEntity;

    @Override
    @Transactional
    public List<ArviointiasteikkoDto> getAll() {
        List<Arviointiasteikko> arviointiasteikot = repository.findAll();

        // Ladataan perusteesta
        if (arviointiasteikot.isEmpty()) {
            arviointiasteikot = getAllFromPeruste();
        }

        return mapper.mapAsList(arviointiasteikot, ArviointiasteikkoDto.class);
    }

    @Transactional
    private List<Arviointiasteikko> getAllFromPeruste() {
        ResponseEntity<List<ArviointiasteikkoDto>> res = new RestTemplate()
                .exchange(eperusteetServiceUrl + "/api/arviointiasteikot",
                        HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<ArviointiasteikkoDto>>() {
                        });
        List<Arviointiasteikko> arviointiasteikot = mapper.mapAsList(res.getBody(), Arviointiasteikko.class);

        arviointiasteikot.forEach(asteikko -> repository.save(asteikko));
        return arviointiasteikot;
    }

    @Override
    @Transactional
    public ArviointiasteikkoDto get(Long id) {
        if (repository.count() == 0) {
            getAllFromPeruste();
        }

        Arviointiasteikko arviointiasteikko = repository.findOne(id);
        return mapper.map(repository.findOne(id), ArviointiasteikkoDto.class);
    }
}
