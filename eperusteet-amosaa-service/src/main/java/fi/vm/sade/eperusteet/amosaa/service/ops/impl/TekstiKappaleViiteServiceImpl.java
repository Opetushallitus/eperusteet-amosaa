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

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockManager;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.amosaa.service.teksti.TekstiKappaleService;
import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mikkom
 */
@Service
@Transactional(readOnly = true)
public class TekstiKappaleViiteServiceImpl implements TekstiKappaleViiteService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private TekstikappaleviiteRepository repository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private LockManager lockMgr;

    @Override
    public <T> T getTekstiKappaleViite(@P("ktId") Long ktId, Long opsId, Long viiteId, Class<T> t) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, t);
    }

    @Override
    public <T> List<T> getTekstiKappaleViitteet(Long ktId, Long id, Class<T> t) {
        List<TekstiKappaleViite> tkvs = repository.findAllByOwnerId(id);
        return mapper.mapAsList(tkvs, t);
    }

    @Override
    public TekstiKappaleViiteDto.Matala getTekstiKappaleViite(Long ktId, Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Matala addTekstiKappaleViite(
            @P("ktId") Long ktId,
            @P("id") Long id, Long viiteId,
        TekstiKappaleViiteDto.Matala viiteDto) {
        TekstiKappaleViite parentViite = findViite(id, viiteId);
        TekstiKappaleViite uusiViite = mapper.map(viiteDto, TekstiKappaleViite.class);
        uusiViite.setOwner(parentViite.getOwner());
        viiteDto.setTekstiKappale(tekstiKappaleService.add(uusiViite, viiteDto.getTekstiKappale()));
        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);

        uusiViite = repository.save(uusiViite);
        return mapper.map(uusiViite, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto updateTekstiKappaleViite(Long ktId, Long opsId, Long viiteId, TekstiKappaleViiteDto uusi) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (viite.getOwner() != ops) {
            throw new BusinessRuleViolationException("vain-oman-editointi-sallittu");
        }

        // Nopea ratkaisu sisällön häviämiseen, korjaantuu oikein uuden näkymän avulla
        if (uusi.getTekstiKappale().getTeksti() == null) {
            uusi.getTekstiKappale().setTeksti(mapper.map(viite.getTekstiKappale(), TekstiKappaleDto.class).getTeksti());
        }

//        repository.lock(viite.getRoot());
//        lockMgr.lock(viite.getTekstiKappale().getId());
        updateTekstiKappale(opsId, viite, uusi.getTekstiKappale(), false);
        viite.setPakollinen(uusi.isPakollinen());
        viite.setValmis(uusi.isValmis());
        viite = repository.save(viite);
        return mapper.map(viite, TekstiKappaleViiteDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeTekstiKappaleViite(Long ktId, Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);

        if (viite.getVanhempi() == null) {
            throw new BusinessRuleViolationException("Sisällön juurielementtiä ei voi poistaa");
        }

        if (viite.getLapset() != null && !viite.getLapset().isEmpty()) {
            throw new BusinessRuleViolationException("Sisällöllä on lapsia, ei voida poistaa");
        }

        if (viite.isPakollinen()) {
            throw new BusinessRuleViolationException("Pakollista tekstikappaletta ei voi poistaa");
        }

//        repository.lock(viite.getRoot());
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        poistetutService.lisaaPoistettu(koulutustoimija, ops, viite);

        viite.getVanhempi().getLapset().remove(viite);
        repository.delete(viite);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(Long ktId, Long opsId, Long viiteId) {
        TekstiKappaleViite viite = findViite(opsId, viiteId);
        TekstiKappale originaali = viite.getTekstiKappale();
        // TODO: Tarkista onko tekstikappaleeseen lukuoikeutta
        TekstiKappale klooni = originaali.copy();
        klooni.setTila(Tila.LUONNOS);
        viite.setTekstiKappale(tekstiKappaleRepository.save(klooni));
        viite = repository.save(viite);
        return mapper.map(viite, TekstiKappaleViiteDto.Puu.class);
    }

    private List<TekstiKappaleViite> findViitteet(Long id, Long viiteId) {
        TekstiKappaleViite viite = findViite(id, viiteId);
        return repository.findAllByTekstiKappale(viite.getTekstiKappale());
    }

    private TekstiKappaleViite findViite(Long id, Long viiteId) {
        return assertExists(repository.findOneByOwnerIdAndId(id, viiteId), "Tekstikappaleviitettä ei ole olemassa");
    }

    private void clearChildren(TekstiKappaleViite viite, Set<TekstiKappaleViite> refs) {
        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            refs.add(lapsi);
            clearChildren(lapsi, refs);
        }
        viite.setVanhempi(null);
        viite.getLapset().clear();
    }

    private void updateTekstiKappale(Long id, TekstiKappaleViite viite, TekstiKappaleDto uusiTekstiKappale, boolean requireLock) {
        if (uusiTekstiKappale != null) {
            if (Objects.equals(id, viite.getOwner().getId())) {
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
    public TekstiKappaleViite kopioiHierarkia(TekstiKappaleViite original, Opetussuunnitelma owner) {
        TekstiKappaleViite result = new TekstiKappaleViite();
        result.setTekstiKappale(original.getTekstiKappale());
        result.setOwner(owner);
        List<TekstiKappaleViite> lapset = original.getLapset();

        if (lapset != null) {
            for (TekstiKappaleViite lapsi : lapset) {
                TekstiKappaleViite uusiLapsi = kopioiHierarkia(lapsi, owner);
                uusiLapsi.setVanhempi(result);
                result.getLapset().add(uusiLapsi);
            }
        }
        return repository.save(result);
    }

    private TekstiKappaleViite updateTraverse(Long ktId, TekstiKappaleViite parent, TekstiKappaleViiteDto.Puu uusi,
        Set<TekstiKappaleViite> refs) {
        TekstiKappaleViite viite = repository.findOne(uusi.getId());
        if (viite == null || !refs.remove(viite)) {
            throw new BusinessRuleViolationException("Viitepuun päivitysvirhe, annettua alipuun juuren viitettä ei löydy");
        }
        viite.setVanhempi(parent);

        List<TekstiKappaleViite> lapset = viite.getLapset();
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
    public void reorderSubTree(Long ktId, Long opsId, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi) {
        TekstiKappaleViite viite = findViite(opsId, rootViiteId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);

        if (ops == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei olemassa.");
        }

        repository.lock(viite.getRoot());
        Set<TekstiKappaleViite> refs = Collections.newSetFromMap(new IdentityHashMap<>());
        refs.add(viite);
        TekstiKappaleViite parent = viite.getVanhempi();
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
        return mapper.map(repository.findRevision(opsId, rev), TekstiKappaleViite.class);
    }

    @Override
    public Revision getRemoved(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

}
