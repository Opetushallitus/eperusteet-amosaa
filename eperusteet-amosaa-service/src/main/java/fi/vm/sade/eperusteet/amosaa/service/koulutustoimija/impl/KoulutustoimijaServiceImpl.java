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
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YstavaStatus;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.HashSet;
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
    public KoulutustoimijaDto updateKoulutustoimija(Long ktId, KoulutustoimijaDto ktDto) {
        Koulutustoimija toimija = repository.findOne(ktId);
        if (ktDto.getYstavat() == null) {
            ktDto.setYstavat(new HashSet<>());
        }
        Koulutustoimija uusi = mapper.map(ktDto, Koulutustoimija.class);
        uusi.getYstavat().remove(toimija);
        toimija.setKuvaus(uusi.getKuvaus());
        toimija.setYstavat(uusi.getYstavat());
        toimija.getYstavat();
        toimija.setSalliystavat(uusi.isSalliystavat());
        return mapper.map(toimija, KoulutustoimijaDto.class);
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
    public KoulutustoimijaDto getKoulutustoimija(String idTaiOid) {
        Koulutustoimija result;
        try {
            result = repository.findOne(Long.parseLong(idTaiOid));
        }
        catch (NumberFormatException ex) {
            result = repository.findOneByOrganisaatio(idTaiOid);
        }
        return mapper.map(result, KoulutustoimijaDto.class);
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(Long kId) {
        return mapper.map(repository.findOne(kId), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(String ktOid) {
        return mapper.map(repository.findOneByOrganisaatio(ktOid), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public <T> List<T> getPaikallisetTutkinnonOsat(Long ktId, Class<T> tyyppi) {
        Koulutustoimija kt = repository.findOne(ktId);
        return mapper.mapAsList(sisaltoviiteRepository.findAllPaikallisetTutkinnonOsat(kt), tyyppi);
    }

    @Override
    public List<KoulutustoimijaYstavaDto> getOmatYstavat(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        List<KoulutustoimijaYstavaDto> result = kt.getYstavat().stream()
            .map(ystava -> {
                KoulutustoimijaYstavaDto ystavaDto = mapper.map(ystava, KoulutustoimijaYstavaDto.class);
                ystavaDto.setStatus(ystava.getYstavat() != null && ystava.getYstavat().contains(kt)
                        ? YstavaStatus.YHTEISTYO
                        : YstavaStatus.ODOTETAAN);
                return ystavaDto;
            })
            .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(Long ktId) {
        return mapper.mapAsList(repository.findAllYstavalliset(), KoulutustoimijaBaseDto.class);
    }

    @Override
    public List<KoulutustoimijaBaseDto> getPyynnot(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        Set<Koulutustoimija> pyynnot = repository.findAllYstavaPyynnotForKoulutustoimija(kt);
        pyynnot.removeAll(kt.getYstavat());
        return mapper.mapAsList(pyynnot, KoulutustoimijaBaseDto.class);
    }

}
