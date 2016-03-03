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

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.YhteisetRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.YhteisetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nkala
 */
@Service
@Transactional
public class YhteisetServiceImpl implements YhteisetService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private YhteisetRepository repository;

    @Override
    public YhteisetDto getYhteiset(Long kid, Long id) {
        return mapper.map(repository.findOne(id), YhteisetDto.class);
    }

    @Override
    public List<PoistettuDto> getYhteisetPoistetut(Long kid, Long id) {
        ArrayList<PoistettuDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public YhteisetDto updateYhteiset(Long kid, Long id, YhteisetDto body) {
        Yhteiset yhteinen = repository.findOne(id);
        body.setId(id);
        body.setTila(yhteinen.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        Yhteiset updated = mapper.map(body, yhteinen);
        return mapper.map(updated, YhteisetDto.class);
    }

    @Override
    public YhteisetSisaltoDto getYhteisetSisalto(Long kid, Long id) {
        YhteisetSisaltoDto result = new YhteisetSisaltoDto();
        return result;
    }

    @Override
    public YhteisetRepository getRepository() {
        return repository;
    }

    @Override
    public Object convertToDto(Object obj) {
        return mapper.map(obj, YhteisetDto.class);
    }

}
