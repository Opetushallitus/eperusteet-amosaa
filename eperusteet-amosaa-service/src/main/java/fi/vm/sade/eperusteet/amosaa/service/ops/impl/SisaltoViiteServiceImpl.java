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

import fi.vm.sade.eperusteet.amosaa.domain.Poistettu;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.PoistettuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.locks.contexts.SisaltoViiteCtx;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.LockingException;
import fi.vm.sade.eperusteet.amosaa.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockManager;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.teksti.TekstiKappaleService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

/**
 * @author mikkom
 */
@Service
@Transactional(readOnly = true)
public class SisaltoViiteServiceImpl extends AbstractLockService<SisaltoViiteCtx> implements SisaltoViiteService {

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
    PoistettuRepository poistetutRepository;

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
        uusiViite.setVersio(0L);
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
                uusiViite.setSuorituspolku(new Suorituspolku());
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
    public SisaltoViiteDto restoreSisaltoViite(Long ktId, Long opsId, Long poistettuId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        Poistettu poistettu = poistetutRepository.findByKoulutustoimijaAndOpetussuunnitelmaAndId(kt, ops, poistettuId);
        if (poistettu == null) {
            throw new BusinessRuleViolationException("palautettavaa-ei-olemassa");
        }

        SisaltoViite parentViite = repository.findOneRoot(ops);
        SisaltoViiteDto pelastettu = getData(opsId, poistettu.getPoistettu(), poistettu.getRev());
        SisaltoViite uusiViite = mapper.map(pelastettu, SisaltoViite.class);
        uusiViite = SisaltoViite.copy(uusiViite);
        uusiViite.setOwner(ops);
        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);
        uusiViite.setTekstiKappale(null);
        mapTutkinnonParts(uusiViite.getTosa());
        uusiViite = repository.save(uusiViite);
        pelastettu.getTekstiKappale().setId(null);
        tekstiKappaleService.add(uusiViite, pelastettu.getTekstiKappale());
        poistetutRepository.delete(poistettu);
        return mapper.map(uusiViite, SisaltoViiteDto.class);
    }

    private void updateSuorituspolku(SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!uusi.getSuorituspolku().getId().equals(viite.getSuorituspolku().getId())) {
            throw new BusinessRuleViolationException("suorituspolkua-ei-voi-vaihtaa");
        }

        Suorituspolku sp = mapper.map(uusi.getSuorituspolku(), Suorituspolku.class);
        for (SuorituspolkuRivi rivi : sp.getRivit()) {
            rivi.setSuorituspolku(sp);
        }
        viite.setSuorituspolku(sp);
    }

    @Transactional(readOnly = false)
    private void mapTutkinnonParts(Tutkinnonosa tosa) {
        if (tosa == null) {
            return;
        }

        if (tosa.getTyyppi().equals(TutkinnonosaTyyppi.OMA)
                && tosa.getOmatutkinnonosa() != null
                && tosa.getOmatutkinnonosa().getAmmattitaitovaatimuksetLista() != null) {

            tosa.getOmatutkinnonosa().getAmmattitaitovaatimuksetLista().stream()
                .filter(ammattitaitovaatimuksenKohdealue -> ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet() != null)
                .forEach(ammattitaitovaatimuksenKohdealue -> ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet().stream()
                    .forEach(ammattitaitovaatimuksenKohde -> {

                        ammattitaitovaatimuksenKohde.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);

                        if (ammattitaitovaatimuksenKohde.getVaatimukset() != null) {
                            ammattitaitovaatimuksenKohde.getVaatimukset().stream()
                                .forEach(ammattitaitovaatimus -> ammattitaitovaatimus.setAmmattitaitovaatimuksenkohde(ammattitaitovaatimuksenKohde));
                        }
                    }));
        }

        for (TutkinnonosaToteutus toteutus : tosa.getToteutukset()) {
            toteutus.setTutkinnonosa(tosa);
        }
    }

    @Transactional(readOnly = false)
    private void updateTutkinnonOsa(Opetussuunnitelma ops, SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getTosa().getId(), viite.getTosa().getId())) {
            throw new BusinessRuleViolationException("tutkinnonosan-viitetta-ei-voi-vaihtaa");
        }

        Tutkinnonosa tosa = viite.getTosa();

        switch (tosa.getTyyppi()) {
            case OMA:
                break;
            case PERUSTEESTA:
                LokalisoituTeksti nimi = viite.getTekstiKappale().getNimi();
                if (!nimi.getTeksti().equals(uusi.getTekstiKappale().getNimi().getTekstit())) {
                    throw new BusinessRuleViolationException("perusteen-tutkinnon-osan-nimea-ei-voi-muuttaa");
                }
                break;
            default:
                break;
        }

        Tutkinnonosa mappedTosa = mapper.map(uusi.getTosa(), Tutkinnonosa.class);

        // Tulevaisuudessa mahdollista lisätä eri lähteitä muille tyypeille jos koetaan tarpeelliseksi
        if (TutkinnonosaTyyppi.OMA.equals(mappedTosa.getTyyppi())) {
            if (mappedTosa.getOmatutkinnonosa() != null) {
                mappedTosa.getOmatutkinnonosa().setKoodiPrefix(ops.getKoulutustoimija().getOrganisaatio());
            }
        }

        mapTutkinnonParts(mappedTosa);
        viite.setTosa(mappedTosa);
    }

    @Override
    @Transactional(readOnly = false)
    public SisaltoViiteDto updateSisaltoViite(Long ktId, Long opsId, Long viiteId, SisaltoViiteDto uusi) {
        SisaltoViite viite = findViite(opsId, viiteId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (viite.getOwner() != ops) {
            throw new BusinessRuleViolationException("vain-oman-editointi-sallittu");
        }

        repository.lock(viite.getRoot());
        lockMgr.lock(viite.getTekstiKappale().getId());

        if (ops.getTyyppi() != OpsTyyppi.POHJA) {
            uusi.setLiikkumaton(viite.isLiikkumaton());
        } else {
            viite.setPakollinen(uusi.isPakollinen());
            viite.setLiikkumaton(uusi.isLiikkumaton());
            viite.setOhjeteksti(LokalisoituTeksti.of(uusi.getOhjeteksti()));
            viite.setPerusteteksti(LokalisoituTeksti.of(uusi.getPerusteteksti()));
        }

        switch (viite.getTyyppi()) {
            case TUTKINNONOSA:
                updateTutkinnonOsa(ops, viite, uusi);
                break;
            case SUORITUSPOLKU:
                updateSuorituspolku(viite, uusi);
                break;
            case SUORITUSPOLUT:
            case TUTKINNONOSAT:
                viite.setPakollinen(true);
            default:
                break;
        }

        repository.setRevisioKommentti(uusi.getKommentti());
        updateTekstiKappale(opsId, viite, uusi.getTekstiKappale(), false);
        viite.setValmis(uusi.isValmis());
        viite.setVersio((viite.getVersio() != null ? viite.getVersio() : 0) + 1);
        viite = repository.save(viite);
        SisaltoViiteDto result = mapper.map(viite, SisaltoViiteDto.class);
        return result;
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

    private SisaltoViite findViite(Long opsId, Long viiteId, SisaltoTyyppi tyyppi) {
        SisaltoViite result = findViite(opsId, viiteId);
        if (result.getTyyppi() != tyyppi) {
            throw new BusinessRuleViolationException("sisalto-viite-tyyppi-vaara");
        }
        return result;
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

    private void kaikkiPuunAvaimet(SisaltoViite puu, List<Long> result) {
        result.add(puu.getId());
        for (SisaltoViite sv : puu.getLapset()) {
            kaikkiPuunAvaimet(sv, result);
        }
    }

    private void kaikkiPuunAvaimet(SisaltoViiteDto.Puu puu, List<Long> result) {
        result.add(puu.getId());
        for (SisaltoViiteDto.Puu sv : puu.getLapset()) {
            kaikkiPuunAvaimet(sv, result);
        }
    }

    private void validoiRakenne(SisaltoViite vanha, SisaltoViiteDto.Puu uusi) {
        // Tarkista ettei rakenteen elementit muutu järjestyksen vaihtuessa
        List<Long> vanhatAvaimet = new ArrayList<>();
        List<Long> uudetAvaimet = new ArrayList<>();
        kaikkiPuunAvaimet(vanha, vanhatAvaimet);
        kaikkiPuunAvaimet(uusi, uudetAvaimet);
        Collections.sort(vanhatAvaimet);
        Collections.sort(uudetAvaimet);

        if (!vanhatAvaimet.equals(uudetAvaimet)) {
            throw new BusinessRuleViolationException("solmuja-ei-saa-lisata-eika-poistaa-jarjestamisessa");
        }

        if (!Objects.equals(vanha.getId(), uusi.getId())) {
            throw new BusinessRuleViolationException("puun-juurisolmua-ei-voi-vaihtaa");
        }

        // TODO: Tutkinnon osat vain yksi
        // TODO: Tutkinnon osien ryhmät
        // TODO: Suorituspolut vain yksi
        // TODO: Suorituspolkujen toteutukset suorituspolut-otsikon alla
        // TODO: Suorituspolun ryhmän lapsen määritys olemassa koulutuksenjärjestäjällä
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
    public RevisionDto getLatestRevision(Long opsId, Long viiteId) {
        return mapper.map(repository.getLatestRevision(viiteId), RevisionDto.class);
    }

    @Override
    public SisaltoViiteDto getData(Long opsId, Long viiteId, Integer revId) {
        SisaltoViite viite = repository.findRevision(viiteId, revId);
        SisaltoViiteDto svDto = mapper.map(viite, SisaltoViiteDto.class);
        return svDto;
    }

    @Override
    public List<RevisionDto> getRevisions(Long opsId, Long viiteId) {
        SisaltoViite viite = repository.findOne(viiteId);
        if (!Objects.equals(opsId, viite.getOwner().getId())) {
            throw new BusinessRuleViolationException("viitteen-taytyy-kuulua-opetussuunnitelmaan");
        }
        List<Revision> revisions = repository.getRevisions(viiteId);
        return mapper.mapAsList(revisions, RevisionDto.class);
    }

    @Override
    public void revertToVersion(Long opsId, Long viiteId, Integer versio) {
    }

    private List<SisaltoViite> getByKoodiRaw(Long ktId, String koodi) {
        if (koodi == null) {
            return new ArrayList<>();
        }

        String[] osat = koodi.split("_");
        if (osat.length == 4 && koodi.startsWith("paikallinen_tutkinnonosa")) {
            String ktOrg = osat[2];
            String osaKoodi = osat[3];
            Koulutustoimija kt = koulutustoimijaRepository.findOneByOrganisaatio(ktOrg);
            return repository.findAllPaikallisetTutkinnonOsatByKoodi(kt, osaKoodi);
        }
        else if (koodi.startsWith("tutkinnonosat_")) {
            Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
            return repository.findAllTutkinnonOsatByKoodi(kt, koodi);
        }

        return new ArrayList<>();
    }

    @Override
    public <T> List<T> getByKoodi(Long ktId, String koodi, Class<T> tyyppi) {
        return mapper.mapAsList(getByKoodiRaw(ktId, koodi), tyyppi);
    }

    @Override
    public <T> List<T> getByKoodiJulkinen(Long ktId, String koodi, Class<T> tyyppi) {
        return mapper.mapAsList(getByKoodiRawJulkinen(ktId, koodi), tyyppi);
    }

    private List<SisaltoViite> getByKoodiRawJulkinen(Long ktId, String koodi) {
        if (koodi == null) {
            return new ArrayList<>();
        }

        String[] osat = koodi.split("_");
        if (osat.length == 4 && koodi.startsWith("paikallinen_tutkinnonosa")) {
            String ktOrg = osat[2];
            String osaKoodi = osat[3];
            Koulutustoimija kt = koulutustoimijaRepository.findOneByOrganisaatio(ktOrg);

            return repository.findAllPaikallisetTutkinnonOsatByKoodi(kt, osaKoodi).stream()
                    .filter(k -> opsRepository.isEsikatseltavissa(k.getOwner().getId()))
                    .collect(Collectors.toList());
        } else if (koodi.startsWith("tutkinnonosat_")) {
            Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);

            return repository.findAllTutkinnonOsatByKoodi(kt, koodi).stream()
                    .filter(k -> opsRepository.isEsikatseltavissa(k.getOwner().getId()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public int getCountByKoodi(Long ktId, String koodi) {
        return getByKoodiRaw(ktId, koodi).size();
    }

    @Override
    protected Long getLockId(SisaltoViiteCtx ctx) {
        return ctx.getSvId();
    }

    @Override
    protected Long validateCtx(SisaltoViiteCtx ctx, boolean readOnly) {
        SisaltoViite sv = repository.findOneByOwnerIdAndId(ctx.getOpsId(), ctx.getSvId());
        if (sv != null) {
            return ctx.getSvId();
        }
        throw new LockingException("virheellinen-lukitus");
    }

    @Override
    protected int latestRevision(SisaltoViiteCtx ctx) {
        return repository.getLatestRevisionId(ctx.getSvId());
    }


}
