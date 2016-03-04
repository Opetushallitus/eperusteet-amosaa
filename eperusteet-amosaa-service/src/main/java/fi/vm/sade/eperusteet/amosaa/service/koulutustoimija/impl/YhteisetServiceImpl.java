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

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.YhteisetRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.YhteisetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private YhteisetRepository repository;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajanTietoService kayttajatietoService;

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
    public KayttajaoikeusDto updateOikeus(Long baseId, Long id, Long oikeusId, KayttajaoikeusDto oikeusDto) {
        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOne(oikeusId);

        if (!Objects.equals(oikeus.getKoulutustoimija().getId(), baseId)
                || !Objects.equals(oikeus.getYhteiset().getId(), id)
                || !oikeusDto.getKayttaja().getId().equals(oikeusDto.getKayttaja().getId())) {
            throw new BusinessRuleViolationException("Päivitys virheellinen");
        }
        oikeus.setOikeus(oikeusDto.getOikeus());
        return mapper.map(oikeus, KayttajaoikeusDto.class);
    }

    @Override
    public List<KayttajaoikeusDto> getOikeudet(Long baseId, Long id) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(baseId);
        Yhteiset yhteiset = kt.getYhteiset();
        if (!Objects.equals(yhteiset.getId(), id)) {
            throw new BusinessRuleViolationException("Yhteiset eivät ole sama kuin koulutustoimijalla");
        }
        List<KayttajaoikeusDto> oikeusDtot = mapper.mapAsList(kayttajaoikeusRepository.findAllByKoulutustoimijaAndYhteiset(kt, yhteiset), KayttajaoikeusDto.class);
        for (KayttajaoikeusDto oikeus : oikeusDtot) {
            oikeus.setKayttajatieto(kayttajatietoService.haeNimi(oikeus.getKayttaja().getIdLong()));
        }
        return oikeusDtot;
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
