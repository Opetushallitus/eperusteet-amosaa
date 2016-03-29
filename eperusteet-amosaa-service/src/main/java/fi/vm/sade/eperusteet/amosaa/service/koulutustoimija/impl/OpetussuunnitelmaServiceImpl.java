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

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;
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
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private TekstikappaleviiteRepository tkvRepository;

    @Autowired
    private TekstiKappaleViiteService tkvService;

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(Long ktId) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimija(koulutustoimija);
        return mapper.mapAsList(opsit, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmatByTyyppi(OpsTyyppi tyyppi) {
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAllByTyyppi(tyyppi);
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(Long ktId, OpetussuunnitelmaDto opsDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = mapper.map(opsDto, Opetussuunnitelma.class);
        ops.setKoulutustoimija(kt);
        ops.setTila(Tila.LUONNOS);
        ops = repository.save(ops);

        if (kt.isOph()) {
            opsDto.setTyyppi(OpsTyyppi.POHJA);
        }
        else {
            switch (opsDto.getTyyppi()) {
                case OPS:
                    // FIXME tarkista perusteen löytyminen

                    break;
                case YHTEINEN:
                    Opetussuunnitelma pohja = repository.findOne(opsDto.getPohja().getIdLong());
                    if (pohja == null) {
                        throw new BusinessRuleViolationException("pohjaa-ei-loytynyt");
                    }
                    TekstiKappaleViite pohjatkv = tkvRepository.findOneRoot(pohja);
                    tkvRepository.save(tkvService.kopioiHierarkia(pohjatkv, ops));
                    ops.setPohja(pohja);
                    break;
                case YLEINEN:
                    break;
                case POHJA:
                    throw new BusinessRuleViolationException("ainoastaan-oph-voi-tehda-pohjia");
                default:
                    throw new BusinessRuleViolationException("opstyypille-ei-toteutusta");
            }
        }

        if (opsDto.getTyyppi() != OpsTyyppi.YHTEINEN) {
            TekstiKappaleViite tkv = new TekstiKappaleViite();
            tkv.setOwner(ops);
            tkvRepository.save(tkv);
        }
        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
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
    public OpetussuunnitelmaDto update(Long ktId, Long opsId, OpetussuunnitelmaDto body) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        body.setId(opsId);
        body.setTila(ops.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        Opetussuunnitelma updated = mapper.map(body, ops);
        return mapper.map(updated, OpetussuunnitelmaDto.class);
    }

    @Override
    public KayttajaoikeusDto updateOikeus(Long ktId, Long opsId, Long kayttajaId, KayttajaoikeusDto oikeusDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);

        // FIXME lisää oikeustarkistelu muokkaajalle
        
        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOneByKayttajaAndOpetussuunnitelma(kayttaja, ops);

        if (oikeusDto.getOikeus() == KayttajaoikeusTyyppi.LUKU) {
            if (oikeus != null) {
                kayttajaoikeusRepository.delete(oikeus);
            }
            return null;
        }

        if (oikeus == null) {
            oikeus = new Kayttajaoikeus();
            oikeus.setKoulutustoimija(kt);
            oikeus.setKayttaja(kayttaja);
            oikeus.setOpetussuunnitelma(ops);
        }

        oikeus.setOikeus(oikeusDto.getOikeus());
        oikeus = kayttajaoikeusRepository.save(oikeus);
        return mapper.map(oikeus, KayttajaoikeusDto.class);
    }

    @Override
    public List<KayttajaoikeusDto> getOikeudet(Long ktId, Long opsId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        List<KayttajaoikeusDto> oikeusDtot = mapper.mapAsList(kayttajaoikeusRepository.findAllByKoulutustoimijaAndOpetussuunnitelma(kt, ops), KayttajaoikeusDto.class);
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
