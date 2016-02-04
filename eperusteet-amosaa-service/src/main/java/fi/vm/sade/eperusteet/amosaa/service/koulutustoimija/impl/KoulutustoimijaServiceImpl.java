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

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.TyoryhmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author nkala
 */
@Service
public class KoulutustoimijaServiceImpl implements KoulutustoimijaService {
    private KoulutustoimijaDto getOrInitialize(String kOid) {
        KoulutustoimijaDto koulutustoimijaDto = new KoulutustoimijaDto();
        koulutustoimijaDto.setId(1L);
        koulutustoimijaDto.setOid(kOid);
//        koulutustoimijaDto.setOid("1.2.246.562.10.20516711478"););
        koulutustoimijaDto.setVersion(1L);
        return koulutustoimijaDto;
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(String kOid) {
        return getOrInitialize(kOid);
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(Long kId) {
        KoulutustoimijaDto result = new KoulutustoimijaDto();
        return result;
    }

    @Override
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat() {
        ArrayList<KoulutustoimijaBaseDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public YhteinenDto getYhteinen(String kOid) {
        YhteinenDto result = new YhteinenDto();
        return result;
    }

    @Override
    public List<PoistettuDto> getYhteinenPoistetut(String kOid) {
        ArrayList<PoistettuDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<TyoryhmaDto> getTyoryhmat(String kOid) {
        ArrayList<TyoryhmaDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public YhteinenSisaltoDto getYhteinenSisalto(String kOid) {
        YhteinenSisaltoDto result = new YhteinenSisaltoDto();
        return result;
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(String kOid) {
        ArrayList<OpetussuunnitelmaBaseDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(String kOid, Long opsId) {
        OpetussuunnitelmaDto result = new OpetussuunnitelmaDto();
        return result;
    }

    @Override
    public List<TiedoteDto> getTiedotteet(String kOid) {
        ArrayList<TiedoteDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public TiedoteDto getTiedote(String kOid) {
        TiedoteDto result = new TiedoteDto();
        return result;
    }

    @Override
    public List<TiedoteDto> getOmatTiedotteet(String kOid) {
        ArrayList<TiedoteDto> result = new ArrayList<>();
        return result;
    }
}
