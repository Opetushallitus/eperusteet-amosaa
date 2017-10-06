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

package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Suoritustapakoodi;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nkala
 */
@Service
@Transactional(readOnly = true)
public class PerusteCacheServiceImpl implements PerusteCacheService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    CachedPerusteRepository repository;

    @Autowired
    EperusteetService eperusteetService;

    private JsonNode parsePeruste(Long id) {
        CachedPeruste peruste = repository.findOne(id);
        if (peruste == null) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(peruste.getPeruste());
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public PerusteDto get(Long id) {
        JsonNode perusteJson = parsePeruste(id);
        PerusteDto peruste = mapper.map(perusteJson, PerusteDto.class);
        return peruste;
    }

    @Override
    public JsonNode getTutkinnonOsa(Long id, Long tosaId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JsonNode getTutkinnonOsat(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SuoritustapaLaajaDto getSuoritustapa(Opetussuunnitelma ops, Long id) {
        PerusteKaikkiDto perusteSisalto = eperusteetService.getPerusteSisalto(id, PerusteKaikkiDto.class);
        for (SuoritustapaLaajaDto suoritustapa : perusteSisalto.getSuoritustavat()) {
            if (suoritustapa.getSuoritustapakoodi() == Suoritustapakoodi.of(ops.getSuoritustapa())) {
                return suoritustapa;
            }
        }

        throw new BusinessRuleViolationException("opetussuunnitelman-vaatimaa-suoritustapaa-ei-loytynyt");
    }
}
