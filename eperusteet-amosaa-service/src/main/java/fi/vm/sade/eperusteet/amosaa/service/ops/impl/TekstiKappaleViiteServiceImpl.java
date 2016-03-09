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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.YhteisetRepository;
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
    private YhteisetRepository yhteisetRepository;

    @Autowired
    private TekstikappaleviiteRepository repository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private LockManager lockMgr;

    @Override
    public <T> T getTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId, Class<T> t) {
        TekstiKappaleViite viite = findViite(id, viiteId);
        return mapper.map(viite, t);
    }

    @Override
    public <T> List<T> getTekstiKappaleViitteet(Long baseId, Long id, Class<T> t) {
        List<TekstiKappaleViite> tkvs = repository.findAllByOwner(id);
        return mapper.mapAsList(tkvs, t);
    }

    @Override
    public TekstiKappaleViiteDto.Matala getTekstiKappaleViite(Long baseId, @P("id") Long id, Long viiteId) {
        TekstiKappaleViite viite = findViite(id, viiteId);
        return mapper.map(viite, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Matala addTekstiKappaleViite(
            @P("baseId") Long baseId,
            @P("id") Long id, Long viiteId,
        TekstiKappaleViiteDto.Matala viiteDto) {
        viiteDto.setOwner(id);

        TekstiKappaleViite parentViite = findViite(id, viiteId);
        TekstiKappaleViite uusiViite = mapper.map(viiteDto, TekstiKappaleViite.class);
        viiteDto.setTekstiKappale(tekstiKappaleService.add(uusiViite, viiteDto.getTekstiKappale()));
        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);

        uusiViite = repository.save(uusiViite);
        return mapper.map(uusiViite, TekstiKappaleViiteDto.Matala.class);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto updateTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId, TekstiKappaleViiteDto uusi) {
        TekstiKappaleViite viite = findViite(id, viiteId);
        // Nopea ratkaisu sisällön häviämiseen, korjaantuu oikein uuden näkymän avulla
        if (uusi.getTekstiKappale().getTeksti() == null) {
            uusi.getTekstiKappale().setTeksti(mapper.map(viite.getTekstiKappale(), TekstiKappaleDto.class).getTeksti());
        }

//        repository.lock(viite.getRoot());
//        lockMgr.lock(viite.getTekstiKappale().getId());
        updateTekstiKappale(id, viite, uusi.getTekstiKappale(), false);
        viite.setPakollinen(uusi.isPakollinen());
        viite.setValmis(uusi.isValmis());
        viite = repository.save(viite);
        return mapper.map(viite, TekstiKappaleViiteDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId) {
        TekstiKappaleViite viite = findViite(id, viiteId);

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
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(baseId);
        poistetutService.lisaaPoistettu(koulutustoimija, koulutustoimija.getYhteiset(), viite);

        viite.getVanhempi().getLapset().remove(viite);
        repository.delete(viite);
    }

    @Override
    @Transactional(readOnly = false)
    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(@P("baseId") Long baseId, @P("id") Long id, Long viiteId) {
        TekstiKappaleViite viite = findViite(id, viiteId);
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
        return assertExists(repository.findOneByOwnerAndId(id, viiteId), "Tekstikappaleviitettä ei ole olemassa");
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
            if (Objects.equals(id, viite.getOwner())) {
                if ( viite.getTekstiKappale() != null ) {
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

    private TekstiKappaleViite updateTraverse(Long baseId, TekstiKappaleViite parent, TekstiKappaleViiteDto.Puu uusi,
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
                .map(elem -> updateTraverse(baseId, viite, elem, refs))
                .collect(Collectors.toList()));
        }
        return repository.save(viite);
    }

    @Override
    @Transactional(readOnly = false)
    public void reorderSubTree(@P("baseId") Long baseId, @P("id") Long id, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi) {
        TekstiKappaleViite viite = findViite(id, rootViiteId);
        Yhteiset ops = yhteisetRepository.findOne(id);

        if (ops == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei olemassa.");
        }

        repository.lock(viite.getRoot());
        Set<TekstiKappaleViite> refs = Collections.newSetFromMap(new IdentityHashMap<>());
        refs.add(viite);
        TekstiKappaleViite parent = viite.getVanhempi();
        clearChildren(viite, refs);
        updateTraverse(baseId, parent, uusi, refs);
    }

}
