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
package fi.vm.sade.eperusteet.amosaa.service.teksti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.teksti.TekstiKappaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

/**
 * @author mikkom
 */
@Service
@Transactional
public class TekstiKappaleServiceImpl implements TekstiKappaleService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private TekstiKappaleRepository repository;

    @Override
    public TekstiKappaleDto add(Long opsId, SisaltoViite viite, TekstiKappaleDto tekstiKappaleInDto) {
        TekstiKappaleDto tekstiKappaleDto = tekstiKappaleInDto != null
                ? tekstiKappaleInDto
                : new TekstiKappaleDto();
        TekstiKappale tekstiKappale = mapper.map(tekstiKappaleDto, TekstiKappale.class);
        tekstiKappale.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappale);
        tekstiKappale = repository.saveAndFlush(tekstiKappale);
        mapper.map(tekstiKappale, tekstiKappaleDto);

        return tekstiKappaleDto;
    }

    @Override
    public TekstiKappaleDto update(Long opsId, TekstiKappaleDto tekstiKappaleDto) {
        Long id = tekstiKappaleDto.getId();
        TekstiKappale current = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa");
        repository.lock(current);
        mapper.map(tekstiKappaleDto, current);

        TekstiKappale paivitetty = repository.save(current);
        return mapper.map(paivitetty, TekstiKappaleDto.class);
    }

    @Override
    public TekstiKappaleDto mergeNew(SisaltoViite viite, TekstiKappaleDto tekstiKappaleDto) {
        if (viite.getTekstiKappale() == null || viite.getTekstiKappale().getId() == null) {
            throw new IllegalArgumentException("Virheellinen viite");
        }
        Long id = viite.getTekstiKappale().getId();
        TekstiKappale clone = assertExists(repository.findOne(id), "Tekstikappaletta ei ole olemassa").copy();
        mapper.map(tekstiKappaleDto, clone);
        clone = repository.save(clone);

        viite.setTekstiKappale(clone);

        mapper.map(clone, tekstiKappaleDto);
        return tekstiKappaleDto;
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
