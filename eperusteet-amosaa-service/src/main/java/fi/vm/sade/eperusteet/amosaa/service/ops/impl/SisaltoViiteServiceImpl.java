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
package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockManager;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.teksti.TekstiKappaleService;
import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mikkom
 */
@Service
@Transactional(readOnly = true)
public class SisaltoViiteServiceImpl implements SisaltoViiteService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private SisaltoviiteRepository repository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private LockManager lockMgr;

    @Override
    public <T> T getSisaltoViite(@P("ktId") Long ktId, Long opsId, Long viiteId, Class<T> t) {
        SisaltoViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, t);
    }

    @Override
    public <T> List<T> getSisaltoViitteet(Long ktId, Long opsId, Class<T> t) {
        List<SisaltoViite> tkvs = repository.findAllByOwnerId(opsId);
        return mapper.mapAsList(tkvs, t);
    }

    @Override
    public SisaltoViiteDto.Matala getSisaltoViite(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, SisaltoViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public SisaltoViiteDto.Matala addSisaltoViite(
            Long ktId,
            Long opsId,
            Long viiteId,
            SisaltoViiteDto.Matala viiteDto) {
        // Ei sallita ohjelmallisesti luotujen sisältöjen luomista uudelleen
        if (viiteDto.getTyyppi() == null || !SisaltoTyyppi.salliLuonti(viiteDto.getTyyppi())) {
            throw new BusinessRuleViolationException("ei-sallittu-tyyppi");
        }
        
        // TODO: Tarkistetaan onko sisältö lisätty oikeantyyppiseen sisältöelementtiin

        SisaltoViite parentViite = findViite(opsId, viiteId);
        SisaltoViite uusiViite = mapper.map(viiteDto, SisaltoViite.class);
        uusiViite.setOwner(parentViite.getOwner());
        viiteDto.setTekstiKappale(tekstiKappaleService.add(uusiViite, viiteDto.getTekstiKappale()));
        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);
        uusiViite = repository.save(uusiViite);

        switch (uusiViite.getTyyppi()) {
            case TOSARYHMA:
                if (parentViite.getTyyppi() != SisaltoTyyppi.TUTKINNONOSA) {
                    throw new BusinessRuleViolationException("ryhman-voi-liittaa-ainoastaan-tutkinnonosiin");
                }
                break;
            case SUORITUSPOLKU:
                if (parentViite.getTyyppi() != SisaltoTyyppi.SUORITUSPOLUT) {
                    throw new BusinessRuleViolationException("suorituspolun-voi-liittaa-ainoastaan-suorituspolkuihin");
                }
                break;
            case TUTKINNONOSA:
                if (parentViite.getTyyppi() != SisaltoTyyppi.TUTKINNONOSAT
                        && parentViite.getTyyppi() != SisaltoTyyppi.TOSARYHMA) {
                    throw new BusinessRuleViolationException("tutkinnonosan-voi-liittaa-ainoastaan-tutkinnonosaryhmaan-tai-tutkinnon-osiin");
                }
                Tutkinnonosa tosa = new Tutkinnonosa();
                tosa.setTyyppi(TutkinnonosaTyyppi.OMA);
                uusiViite.setTosa(tutkinnonosaRepository.save(tosa));
                break;
            default:
                break;
        }

        return mapper.map(uusiViite, SisaltoViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void restoreSisaltoViite(Long ktId, Long opsId, Long poistettuId) {
        throw new NotImplementedException("Ei toteutettu vielä.");
    }

    @Transactional(readOnly = false)
    private void updateTutkinnonOsa(SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getTosa().getId(), viite.getTosa().getId())) {
            throw new BusinessRuleViolationException("tutkinnonosan-viitetta-ei-voi-vaihtaa");
        }
        Tutkinnonosa mappedOsa = mapper.map(uusi.getTosa(), Tutkinnonosa.class);
        viite.setTosa(mappedOsa);
    }

    @Override
    @Transactional(readOnly = false)
    public SisaltoViiteDto updateSisaltoViite(Long ktId, Long opsId, Long viiteId, SisaltoViiteDto uusi) {
        SisaltoViite viite = findViite(opsId, viiteId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (viite.getOwner() != ops) {
            throw new BusinessRuleViolationException("vain-oman-editointi-sallittu");
        }

        if (ops.getTyyppi() != OpsTyyppi.POHJA) {
            uusi.setLiikkumaton(viite.isLiikkumaton());
        }
        else {
            viite.setPakollinen(uusi.isPakollinen());
            viite.setLiikkumaton(uusi.isLiikkumaton());
            viite.setOhjeteksti(LokalisoituTeksti.of(uusi.getOhjeteksti()));
            viite.setPerusteteksti(LokalisoituTeksti.of(uusi.getPerusteteksti()));
        }

        // Nopea ratkaisu sisällön häviämiseen, korjaantuu oikein uuden näkymän avulla
        if (uusi.getTekstiKappale().getTeksti() == null) {
            uusi.getTekstiKappale().setTeksti(mapper.map(viite.getTekstiKappale(), TekstiKappaleDto.class).getTeksti());
        }

        switch (viite.getTyyppi()) {
            case TUTKINNONOSA:
                updateTutkinnonOsa(viite, uusi);
                break;
            default:
                break;
        }

        repository.lock(viite.getRoot());
        lockMgr.lock(viite.getTekstiKappale().getId());
        updateTekstiKappale(opsId, viite, uusi.getTekstiKappale(), false);
        viite.setValmis(uusi.isValmis());
        viite = repository.save(viite);
        return mapper.map(viite, SisaltoViiteDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeSisaltoViite(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);

        if (viite.getVanhempi() == null) {
            throw new BusinessRuleViolationException("Sisällön juurielementtiä ei voi poistaa");
        }

        if (viite.getLapset() != null && !viite.getLapset().isEmpty()) {
            throw new BusinessRuleViolationException("Sisällöllä on lapsia, ei voida poistaa");
        }

        if (viite.isPakollinen() && ops.getTyyppi() != OpsTyyppi.POHJA) {
            throw new BusinessRuleViolationException("Pakollista tekstikappaletta ei voi poistaa");
        }

        if (viite.getTyyppi() == SisaltoTyyppi.TUTKINNONOSAT
                || viite.getTyyppi() == SisaltoTyyppi.SUORITUSPOLUT) {
            throw new BusinessRuleViolationException("Pakollisia sisältöjä ei voi poistaa");
        }

//        repository.lock(viite.getRoot());
        poistetutService.lisaaPoistettu(koulutustoimija, ops, viite);

        viite.getVanhempi().getLapset().remove(viite);
        repository.delete(viite);
    }

    @Override
    @Transactional(readOnly = false)
    public SisaltoViiteDto.Puu kloonaaTekstiKappale(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
        TekstiKappale originaali = viite.getTekstiKappale();
        // TODO: Tarkista onko tekstikappaleeseen lukuoikeutta
        TekstiKappale klooni = originaali.copy();
        klooni.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappaleRepository.save(klooni));
        viite = repository.save(viite);
        return mapper.map(viite, SisaltoViiteDto.Puu.class);
    }

    private List<SisaltoViite> findViitteet(Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
        return repository.findAllByTekstiKappale(viite.getTekstiKappale());
    }

    private SisaltoViite findViite(Long opsId, Long viiteId) {
        return assertExists(repository.findOneByOwnerIdAndId(opsId, viiteId), "Tekstikappaleviitettä ei ole olemassa");
    }

    @Transactional(readOnly = false)
    private void clearChildren(SisaltoViite viite, Set<SisaltoViite> refs) {
        for (SisaltoViite lapsi : viite.getLapset()) {
            refs.add(lapsi);
            clearChildren(lapsi, refs);
        }
        viite.setVanhempi(null);
        viite.getLapset().clear();
    }

    private void updateTekstiKappale(Long opsId, SisaltoViite viite, TekstiKappaleDto uusiTekstiKappale, boolean requireLock) {
        if (uusiTekstiKappale != null) {
            if (Objects.equals(opsId, viite.getOwner().getId())) {
                if (viite.getTekstiKappale() != null) {
                    final Long tid = viite.getTekstiKappale().getId();
                    if (requireLock || lockMgr.getLock(tid) != null) {
                        lockMgr.ensureLockedByAuthenticatedUser(tid);
                    }
                }
                tekstiKappaleService.update(uusiTekstiKappale);
            } else {
                throw new BusinessRuleViolationException("Lainattua tekstikappaletta ei voida muokata");
            }
        }
    }

    // Kopioi viitehierarkian ja siirtää irroitetut paikoilleen
    // UUID parentin tunniste
    @Override
    @Transactional(readOnly = false)
    public SisaltoViite kopioiHierarkia(SisaltoViite original, Opetussuunnitelma owner) {
        SisaltoViite result = new SisaltoViite();
        TekstiKappale originalTk = original.getTekstiKappale();
        result.setTekstiKappale(originalTk != null ? tekstiKappaleRepository.save(originalTk.copy()) : null);
        result.setOwner(owner);
        result.setLiikkumaton(original.isLiikkumaton());
        result.setPakollinen(original.isPakollinen());
        result.setOhjeteksti(original.getOhjeteksti());
        result.setPerusteteksti(original.getPerusteteksti());
        List<SisaltoViite> lapset = original.getLapset();

        if (lapset != null) {
            for (SisaltoViite lapsi : lapset) {
                SisaltoViite uusiLapsi = kopioiHierarkia(lapsi, owner);
                uusiLapsi.setVanhempi(result);
                result.getLapset().add(uusiLapsi);
            }
        }
        return repository.save(result);
    }

    private void validoiRakenne(SisaltoViite vanha, SisaltoViiteDto.Puu uusi) {
        // TODO: Tutkinnon osat vain yksi
        // TODO: Tutkinnon osien ryhmät
        // TODO: Suorituspolut vain yksi
        // TODO: Suorituspolkujen toteutukset suorituspolut-otsikon alla
        List<SisaltoViite> lapset = vanha.getLapset();
    }

    @Transactional(readOnly = false)
    private SisaltoViite updateTraverse(Long ktId, SisaltoViite parent, SisaltoViiteDto.Puu uusi,
        Set<SisaltoViite> refs) {
        SisaltoViite viite = repository.findOne(uusi.getId());

        if (viite == null || !refs.remove(viite)) {
            throw new BusinessRuleViolationException("Viitepuun päivitysvirhe, annettua alipuun juuren viitettä ei löydy");
        }
        
        viite.setVanhempi(parent);

        List<SisaltoViite> lapset = viite.getLapset();
        lapset.clear();

        if (uusi.getLapset() != null) {
            lapset.addAll(uusi.getLapset()
                .stream()
                .map(elem -> updateTraverse(ktId, viite, elem, refs))
                .collect(Collectors.toList()));
        }
        return repository.save(viite);
    }

    @Override
    @Transactional(readOnly = false)
    public void reorderSubTree(Long ktId, Long opsId, Long rootViiteId, SisaltoViiteDto.Puu uusi) {
        SisaltoViite viite = findViite(opsId, rootViiteId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);

        if (ops == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei olemassa.");
        }

        validoiRakenne(viite, uusi);

        repository.lock(viite.getRoot());
        Set<SisaltoViite> refs = Collections.newSetFromMap(new IdentityHashMap<>());
        refs.add(viite);
        SisaltoViite parent = viite.getVanhempi();
        clearChildren(viite, refs);
        updateTraverse(ktId, parent, uusi, refs);
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
        return mapper.map(repository.findRevision(opsId, rev), SisaltoViite.class);
    }

    @Override
    public Revision getRemoved(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

}
