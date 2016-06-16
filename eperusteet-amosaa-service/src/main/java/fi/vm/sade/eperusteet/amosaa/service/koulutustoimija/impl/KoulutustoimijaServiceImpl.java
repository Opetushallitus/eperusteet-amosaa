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

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nkala
 */
@Service
@Transactional
public class KoulutustoimijaServiceImpl implements KoulutustoimijaService {
    private static final Logger LOG = LoggerFactory.getLogger(KoulutustoimijaServiceImpl.class);

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutustoimijaRepository repository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private DtoMapper mapper;

    @Transactional(readOnly = false)
    private Koulutustoimija initialize(String kOid) {
        Koulutustoimija koulutustoimija = repository.findOneByOrganisaatio(kOid);
        if (koulutustoimija != null) {
            return koulutustoimija;
        }

        LOG.info("Luodaan uusi organisaatiota vastaava koulutustoimija ensimmäistä kertaa", kOid);
        JsonNode organisaatio = organisaatioService.getOrganisaatio(kOid);
        if (organisaatio == null) {
            return null;
        }

        LokalisoituTeksti nimi = LokalisoituTeksti.of(Kieli.FI, organisaatio.get("nimi").get("fi").asText());
        koulutustoimija = new Koulutustoimija();
        koulutustoimija.setNimi(nimi);
        koulutustoimija.setOrganisaatio(kOid);
        koulutustoimija = repository.save(koulutustoimija);
        return koulutustoimija;
    }

    @Override
    public List<KoulutustoimijaBaseDto> initKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
                .map(this::initialize)
                .filter(kt -> kt != null)
                .map(kt -> mapper.map(kt, KoulutustoimijaBaseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
                .map(ktId -> {
                    LOG.info("Käyttäjän koulutustoimija", ktId);
                    Koulutustoimija kt = repository.findOneByOrganisaatio(ktId);
                    return mapper.map(kt, KoulutustoimijaBaseDto.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(Long kId) {
        return mapper.map(repository.findOne(kId), KoulutustoimijaDto.class);
    }

    @Override
    public <T> List<T> getPaikallisetTutkinnonOsat(Long ktId, Class<T> tyyppi) {
        Koulutustoimija kt = repository.findOne(ktId);
        return mapper.mapAsList(sisaltoviiteRepository.findAllPaikallisetTutkinnonOsat(kt), tyyppi);
    }

}
