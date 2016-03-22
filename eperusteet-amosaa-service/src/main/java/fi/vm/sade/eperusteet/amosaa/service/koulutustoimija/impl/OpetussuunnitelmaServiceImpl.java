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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
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
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajanTietoService kayttajatietoService;

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(Long ktId) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimija(koulutustoimija);
        return mapper.mapAsList(opsit, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public List<PoistettuDto> getPoistetut(Long ktId, Long id) {
        ArrayList<PoistettuDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public OpetussuunnitelmaDto update(Long ktId, Long id, OpetussuunnitelmaDto body) {
        Opetussuunnitelma ops = repository.findOne(id);
        body.setId(id);
        body.setTila(ops.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        Opetussuunnitelma updated = mapper.map(body, ops);
        return mapper.map(updated, OpetussuunnitelmaDto.class);
    }

    @Override
    public KayttajaoikeusDto updateOikeus(Long ktId, Long id, Long oikeusId, KayttajaoikeusDto oikeusDto) {
        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOne(oikeusId);

        if (!Objects.equals(oikeus.getKoulutustoimija().getId(), ktId)
                || !Objects.equals(oikeus.getOpetussuunnitelma().getId(), id)
                || !oikeusDto.getKayttaja().getId().equals(oikeusDto.getKayttaja().getId())) {
            throw new BusinessRuleViolationException("Päivitys virheellinen");
        }
        oikeus.setOikeus(oikeusDto.getOikeus());
        return mapper.map(oikeus, KayttajaoikeusDto.class);
    }

    @Override
    public List<KayttajaoikeusDto> getOikeudet(Long ktId, Long id) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOneYhteinen(kt, OpsTyyppi.YHTEINEN);
        List<KayttajaoikeusDto> oikeusDtot = mapper.mapAsList(kayttajaoikeusRepository.findAllByKoulutustoimijaAndOpetussuunnitelma(kt, ops), KayttajaoikeusDto.class);
        for (KayttajaoikeusDto oikeus : oikeusDtot) {
            oikeus.setKayttajatieto(kayttajatietoService.haeNimi(oikeus.getKayttaja().getIdLong()));
        }
        return oikeusDtot;
    }

    @Override
    public List<Revision> getRevisions(Long ktId, Long opsId) {
        return repository.getRevisions(opsId);
    }

    @Override
    public Revision getLatestRevision(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

    @Override
    public Integer getLatestRevisionId(Long ktId, Long opsId) {
        return repository.getLatestRevisionId(opsId);
        }

    @Override
    public Object getData(Long ktId, Long opsId, Integer rev) {
        return mapper.map(repository.findRevision(opsId, rev), OpetussuunnitelmaDto.class);
    }

    @Override
    public Revision getRemoved(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }
}
