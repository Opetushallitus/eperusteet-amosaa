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
package fi.vm.sade.eperusteet.amosaa.service.ohje.impl;

import fi.vm.sade.eperusteet.amosaa.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.amosaa.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.amosaa.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ohje.OhjeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author nkala
 */
@Service
@Transactional
public class OhjeServiceImpl implements OhjeService {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OhjeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<OhjeDto> getOhjeet() {
        return mapper.mapAsList(repository.findAll(), OhjeDto.class);
    }

    @Override
    public OhjeDto addOhje(OhjeDto dto) {
        Ohje ohje = mapper.map(dto, Ohje.class);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public OhjeDto editOhje(Long id, OhjeDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BusinessRuleViolationException("id-ei-tasmaa");
        }
        Ohje ohje = repository.findOne(id);
        ohje = mapper.map(dto, ohje);
        return mapper.map(ohje, dto);
    }

    @Override
    public void removeOhje(Long id) {
        repository.delete(id);
    }

}
