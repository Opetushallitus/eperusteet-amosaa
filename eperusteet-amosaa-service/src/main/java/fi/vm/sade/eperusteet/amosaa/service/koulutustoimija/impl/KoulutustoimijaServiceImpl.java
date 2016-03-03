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
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
//import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteinen;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.YhteisetRepository;
import javax.persistence.EntityManager;

/**
 *
 * @author nkala
 */
@Service
@Transactional
public class KoulutustoimijaServiceImpl implements KoulutustoimijaService {

    @Autowired
    private EntityManager em;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private YhteisetRepository yhteinenRepository;

    @Autowired
    private DtoMapper mapper;

    private Koulutustoimija getOrInitialize(String kOid) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOneByOrganisaatio(kOid);
        if (koulutustoimija != null) {
            return koulutustoimija;
        }

        JsonNode organisaatio = organisaatioService.getOrganisaatio(kOid);
        LokalisoituTeksti nimi = LokalisoituTeksti.of(Kieli.FI, organisaatio.get("nimi").get("fi").asText());
        koulutustoimija = new Koulutustoimija();
        koulutustoimija.setNimi(nimi);
        koulutustoimija.setOrganisaatio(kOid);

        Yhteiset yhteinen = new Yhteiset();
        yhteinen.setNimi(nimi);
        yhteinen.setJulkaisukielet(Collections.EMPTY_SET);
        yhteinen.setTila(Tila.LUONNOS);
        koulutustoimija.setYhteiset(yhteinen);
        koulutustoimija = koulutustoimijaRepository.save(koulutustoimija);
        koulutustoimija.getYhteiset().getTekstit().setOwner(koulutustoimija.getYhteiset().getId());
        return koulutustoimija;
    }

    @Override
    public KoulutustoimijaBaseDto getKoulutustoimija(String kOid) {
        return mapper.map(getOrInitialize(kOid), KoulutustoimijaBaseDto.class);
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(Long kId) {
        return mapper.map(koulutustoimijaRepository.findOne(kId), KoulutustoimijaDto.class);
    }

    @Override
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat() {
        ArrayList<KoulutustoimijaBaseDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(Long kOid) {
        ArrayList<OpetussuunnitelmaBaseDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(Long kOid, Long opsId) {
        OpetussuunnitelmaDto result = new OpetussuunnitelmaDto();
        return result;
    }

    @Override
    public List<TiedoteDto> getTiedotteet(Long kOid) {
        ArrayList<TiedoteDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public TiedoteDto getTiedote(Long kOid) {
        TiedoteDto result = new TiedoteDto();
        return result;
    }

    @Override
    public List<TiedoteDto> getOmatTiedotteet(Long kOid) {
        ArrayList<TiedoteDto> result = new ArrayList<>();
        return result;
    }
}
