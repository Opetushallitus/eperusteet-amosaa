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

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Poistettu;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Koulutuksenosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.VierasTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaMuokkaustietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliRooli;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Suoritustapakoodi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.VierasTutkinnonosaDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.PoistettuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.VierastutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.locks.contexts.SisaltoViiteCtx;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.LockingException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.locking.AbstractLockService;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockManager;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;
import fi.vm.sade.eperusteet.amosaa.service.teksti.TekstiKappaleService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

/**
 * @author mikkom
 */
@Service
@Slf4j
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
    private VierastutkinnonosaRepository vierastutkinnonosaRepository;

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    PoistettuRepository poistetutRepository;

    @Autowired
    CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    EperusteetService eperusteetService;

    @Autowired
    EperusteetClient eperusteetClient;

    @Autowired
    PerusteCacheService perusteCacheService;

    @Autowired
    SisaltoviiteServiceProvider sisaltoviiteServiceProvider;

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    @Autowired
    private LockManager lockMgr;

    private ObjectMapper objMapper;

    @Override
    public <T> T getSisaltoViite(Long ktId, Long opsId, Long viiteId, Class<T> t) {
        SisaltoViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, t);
    }

    @Override
    public <T> List<T> getSisaltoViitteet(Long ktId, Long opsId, Class<T> t) {
        List<SisaltoViite> tkvs = repository.findAllByOwnerId(opsId);
        return mapper.mapAsList(tkvs, t);
    }

    @Override
    public <T> T getSisaltoRoot(Long ktId, Long opsId, Class<T> t) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        SisaltoViite root = repository.findOneRoot(ops);
        return mapper.map(root, t);
    }

    @Override
    public SisaltoViiteRakenneDto getRakenne(Long ktId, Long opsId) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        SisaltoViite root = repository.findOneRoot(ops);
        SisaltoViiteRakenneDto rakenne = mapper.map(root, SisaltoViiteRakenneDto.class);
        return rakenne;
    }

    @Override
    public SisaltoViiteDto.Matala getSisaltoViite(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
        return mapper.map(viite, SisaltoViiteDto.Matala.class);
    }

    // FIXME: Muut saattavat haluta käyttää tätä myös
    @Transactional(readOnly = false)
    private CachedPeruste addCachedPeruste(PerusteDto peruste) {
        CachedPeruste result = cachedPerusteRepository.findOneByDiaarinumeroAndLuotu(peruste.getDiaarinumero(), peruste.getGlobalVersion().getAikaleima());
        if (result == null) {
            result = new CachedPeruste();
            result.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            result.setDiaarinumero(peruste.getDiaarinumero());
            result.setLuotu(peruste.getGlobalVersion().getAikaleima());
            result.setPeruste(eperusteetClient.getPerusteData(peruste.getId()));
            result.setPerusteId(peruste.getId());
            result = cachedPerusteRepository.save(result);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public SisaltoViiteDto.Matala addSisaltoViite(
            Long ktId,
            Long opsId,
            Long viiteId,
            SisaltoViiteDto.Matala viiteDto
    ) {
        // Ei sallita ohjelmallisesti luotujen sisältöjen luomista uudelleen
        if (viiteDto.getTyyppi() == null || !SisaltoTyyppi.salliLuonti(viiteDto.getTyyppi())) {
            throw new BusinessRuleViolationException("ei-sallittu-tyyppi");
        }

        // TODO: Tarkistetaan onko sisältö lisätty oikeantyyppiseen sisältöelementtiin

        SisaltoViite parentViite = findViite(opsId, viiteId);
        SisaltoViite uusiViite = mapper.map(viiteDto, SisaltoViite.class);
        uusiViite.setVersio(0L);
        uusiViite.setOwner(parentViite.getOwner());
        viiteDto.setTekstiKappale(tekstiKappaleService.add(opsId, uusiViite, viiteDto.getTekstiKappale()));
        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);

        switch (uusiViite.getTyyppi()) {
            case TOSARYHMA:
                if (parentViite.getTyyppi() != SisaltoTyyppi.TUTKINNONOSA) {
                    throw new BusinessRuleViolationException("ryhman-voi-liittaa-ainoastaan-tutkinnonosiin");
                }
                break;
            case OSASUORITUSPOLKU:
            case SUORITUSPOLKU:
                if (parentViite.getTyyppi() != SisaltoTyyppi.SUORITUSPOLUT) {
                    throw new BusinessRuleViolationException("suorituspolun-voi-liittaei-sallittu-tyyppia-ainoastaan-suorituspolkuihin");
                }
                if (uusiViite.getSuorituspolku() == null) {
                    uusiViite.setSuorituspolku(new Suorituspolku());
                }
                break;
            case TUTKINNONOSA:
                if (parentViite.getTyyppi() != SisaltoTyyppi.TUTKINNONOSAT
                        && parentViite.getTyyppi() != SisaltoTyyppi.TOSARYHMA) {
                    throw new BusinessRuleViolationException("tutkinnonosan-voi-liittaa-ainoastaan-tutkinnonosaryhmaan-tai-tutkinnon-osiin");
                }

                Tutkinnonosa tosa = uusiViite.getTosa();

                if (tosa == null) {
                    tosa = new Tutkinnonosa();
                }

                if (tosa.getTyyppi() == TutkinnonosaTyyppi.VIERAS) {
                    tosa.setTyyppi(TutkinnonosaTyyppi.VIERAS);
                    VierasTutkinnonosa vt = tosa.getVierastutkinnonosa();
                    VierasTutkinnonosaDto vtDto = viiteDto.getTosa().getVierastutkinnonosa();
                    PerusteDto peruste = eperusteetClient.getPeruste(vtDto.getPerusteId(), PerusteDto.class);

                    CachedPeruste cperuste = addCachedPeruste(peruste);
                    PerusteKaikkiDto perusteSisalto = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class);
                    Optional<TutkinnonosaKaikkiDto> perusteenTosaOpt = perusteSisalto.getTutkinnonOsat().stream()
                            .filter(osa -> vtDto.getTosaId().equals(osa.getId()))
                            .findFirst();
                    if (perusteenTosaOpt.isPresent()) {
                        TutkinnonosaKaikkiDto perusteenTosa = perusteenTosaOpt.get();
                        uusiViite.getTekstiKappale().setNimi(LokalisoituTeksti.of(perusteenTosa.getNimi()));
                    } else {
                        throw new BusinessRuleViolationException("perusteella-ei-valittua-tutkinnon-osaa");
                    }

                    vt.setPerusteId(vtDto.getPerusteId());
                    vt.setTosaId(vtDto.getTosaId());
                    vt.setCperuste(cperuste);
                    tosa.setVierastutkinnonosa(vierastutkinnonosaRepository.save(vt));
                } else {
                    tosa.setTyyppi(TutkinnonosaTyyppi.OMA);
                }
                uusiViite.setTosa(tutkinnonosaRepository.save(tosa));
                break;
            default:
                break;
        }

        uusiViite = repository.save(uusiViite);
        return mapper.map(uusiViite, SisaltoViiteDto.Matala.class);
    }

    @Override
    @Transactional
    public SisaltoViiteDto restoreSisaltoViite(Long ktId, Long opsId, Long poistettuId) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        Poistettu poistettu = poistetutRepository.findByOpetussuunnitelmaAndId(ops, poistettuId);
        if (poistettu == null) {
            throw new BusinessRuleViolationException("palautettavaa-ei-olemassa");
        }

        SisaltoViiteDto pelastettu = getData(ktId, opsId, poistettu.getPoistettu(), poistettu.getRev());
        SisaltoViite uusiViite = mapper.map(pelastettu, SisaltoViite.class);
        uusiViite = SisaltoViite.copy(uusiViite);
        uusiViite.setOwner(ops);

        SisaltoViite parentViite;
        switch (pelastettu.getTyyppi()) {
            case TUTKINNONOSA:
                parentViite = repository.findTutkinnonosatRoot(ops);
                break;
            case OSASUORITUSPOLKU:
            case SUORITUSPOLKU:
                parentViite = repository.findSuorituspolutRoot(ops);
                break;
            default:
                parentViite = repository.findOneRoot(ops);
                break;
        }

        uusiViite.setVanhempi(parentViite);
        parentViite.getLapset().add(0, uusiViite);
        uusiViite.setTekstiKappale(null);
        mapTutkinnonParts(uusiViite.getTosa());
        uusiViite = repository.save(uusiViite);
        pelastettu.getTekstiKappale().setId(null);
        tekstiKappaleService.add(opsId, uusiViite, pelastettu.getTekstiKappale());
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
        // Poistetaan laajuus, jos ei käytössä muutenkaan
        if (!SisaltoTyyppi.OSASUORITUSPOLKU.equals(uusi.getTyyppi())) {
            sp.setOsasuorituspolkuLaajuus(null);
        }
        // Tyyppi voidaan muuttaa jälkikäteen
        if (SisaltoTyyppi.isSuorituspolku(uusi.getTyyppi())) {
            viite.setTyyppi(uusi.getTyyppi());
        }

        viite.setSuorituspolku(sp);
    }

    @Transactional
    private void mapTutkinnonParts(Tutkinnonosa tosa) {
        if (tosa == null) {
            return;
        }

        if (tosa.getTyyppi().equals(TutkinnonosaTyyppi.OMA)
                && tosa.getOmatutkinnonosa() != null
                && tosa.getOmatutkinnonosa().getAmmattitaitovaatimuksetLista() != null) {

            tosa.getOmatutkinnonosa().getAmmattitaitovaatimuksetLista().stream()
                    .filter(ammattitaitovaatimuksenKohdealue -> ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet() != null)
                    .forEach(ammattitaitovaatimuksenKohdealue -> ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet().forEach(ammattitaitovaatimuksenKohde -> {

                        ammattitaitovaatimuksenKohde.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);

                        if (ammattitaitovaatimuksenKohde.getVaatimukset() != null) {
                            ammattitaitovaatimuksenKohde.getVaatimukset()
                                    .forEach(ammattitaitovaatimus -> ammattitaitovaatimus.setAmmattitaitovaatimuksenkohde(ammattitaitovaatimuksenKohde));
                        }
                    }));
        }

        for (TutkinnonosaToteutus toteutus : tosa.getToteutukset()) {
            toteutus.setTutkinnonosa(tosa);
        }

        for (VapaaTeksti vapaaTeksti : tosa.getVapaat()) {
            vapaaTeksti.setId(null);
        }
    }

    @Transactional
    private void updateTutkinnonOsa(Opetussuunnitelma ops, SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getTosa().getId(), viite.getTosa().getId())) {
            throw new BusinessRuleViolationException("tutkinnonosan-viitetta-ei-voi-vaihtaa");
        }

        Tutkinnonosa tosa = viite.getTosa();

        switch (tosa.getTyyppi()) {
            case OMA:
                break;
            case VIERAS:
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
        mappedTosa.setVierastutkinnonosa(tosa.getVierastutkinnonosa());

        // Tulevaisuudessa mahdollista lisätä eri lähteitä muille tyypeille jos koetaan tarpeelliseksi
        if (TutkinnonosaTyyppi.OMA.equals(mappedTosa.getTyyppi())) {
            if (mappedTosa.getOmatutkinnonosa() != null) {
                OmaTutkinnonosa omatutkinnonosa = mappedTosa.getOmatutkinnonosa();
                omatutkinnonosa.setKoodiPrefix(ops.getKoulutustoimija().getOrganisaatio());
            }
        }

        mapTutkinnonParts(mappedTosa);
        viite.setTosa(mappedTosa);
    }

    @Override
    @Transactional
    public void updateSisaltoViite(Long ktId, Long opsId, Long viiteId, SisaltoViiteDto uusi) {
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
        viite.setNaytaPerusteenTeksti(uusi.isNaytaPerusteenTeksti());
        viite.setNaytaPohjanTeksti(uusi.isNaytaPohjanTeksti());

        switch (viite.getTyyppi()) {
            case TUTKINNONOSA:
                updateTutkinnonOsa(ops, viite, uusi);
                break;
            case OSASUORITUSPOLKU:
            case SUORITUSPOLKU:
                updateSuorituspolku(viite, uusi);
                updateOpetussuunnitelmaPiilotetutSisaltoviitteet(uusi, ops);
                break;
            case SUORITUSPOLUT:
            case TUTKINNONOSAT:
                viite.setPakollinen(true);
                break;
            case OPINTOKOKONAISUUS:
            case KOULUTUKSENOSA:
            case LAAJAALAINENOSAAMINEN:
                sisaltoviiteServiceProvider.updateSisaltoViite(viite, uusi);
            default:
                break;
        }

        repository.setRevisioKommentti(uusi.getKommentti());
        updateTekstiKappale(opsId, viite, uusi.getTekstiKappale(), false);
        viite.setValmis(uusi.isValmis());
        viite.setVersio((viite.getVersio() != null ? viite.getVersio() : 0) + 1);
        repository.save(viite);
    }

    @Override
    @Transactional
    public void updateOpetussuunnitelmaPiilotetutSisaltoviitteet(SisaltoViiteDto uusi, Opetussuunnitelma ops) {
        SuoritustapaLaajaDto suoritustapa = perusteCacheService.getSuoritustapa(ops, ops.getPeruste().getId());
        Set<UUID> piilotetutSisaltoriviUiids = uusi.getSuorituspolku().getRivit().stream()
                .filter(p -> Boolean.TRUE.equals(p.getPiilotettu()))
                .map(SuorituspolkuRiviDto::getRakennemoduuli)
                .collect(Collectors.toSet());

        ops.getTutkintonimikkeet().clear();
        ops.getOsaamisalat().clear();

        Stack<RakenneModuuliDto> stack = new Stack<>();
        stack.push(suoritustapa.getRakenne());

        while (!stack.empty()) {
            RakenneModuuliDto rakenne = stack.pop();

            if (!piilotetutSisaltoriviUiids.contains(rakenne.getTunniste())) {

                if (rakenne.getTutkintonimike() != null
                        && RakenneModuuliRooli.TUTKINTONIMIKE.equals(rakenne.getRooli())) {
                    ops.getTutkintonimikkeet().add(rakenne.getTutkintonimike().getUri());
                }

                if (rakenne.getOsaamisala() != null && RakenneModuuliRooli.OSAAMISALA.equals(rakenne.getRooli())) {
                    ops.getOsaamisalat().add(rakenne.getOsaamisala().getOsaamisalakoodiUri());
                }

                if (rakenne.getOsat() != null) {
                    rakenne.getOsat().stream().filter(e -> e instanceof RakenneModuuliDto)
                            .forEach(e -> stack.push((RakenneModuuliDto) e));
                }
            }

        }
    }

    @Override
    public <T> Page<T> getSisaltoviitteetWithQuery(Long ktId, SisaltoviiteQueryDto query, Class<T> tyyppi) {
        Pageable pageable = new PageRequest(query.getSivu(), query.getSivukoko(), new Sort(Sort.Direction.fromString(query.isSortDesc() ? "DESC" : "ASC"), "nimi.teksti"));
        return repository.findAllWithPagination(ktId, query.getTyyppi(), Kieli.of(query.getKieli()), query.getNimi(), query.getOpetussuunnitelmaId(), query.getOpsTyyppi(), pageable).map(sisaltoviite -> mapper.map(sisaltoviite, tyyppi));
    }

    @Override
    @Transactional
    public void removeSisaltoViite(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = findViite(opsId, viiteId);
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

        poistetutService.lisaaPoistettu(ktId, ops, viite);

        viite.getVanhempi().getLapset().remove(viite);
        repository.delete(viite);
    }

    @Override
    @Transactional
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
        return assertExists(repository.findOneByOwnerIdAndId(opsId, viiteId), "tekstikappaleviite-ei-loydy");
    }

    private SisaltoViite findViite(Long opsId, Long viiteId, SisaltoTyyppi tyyppi) {
        SisaltoViite result = findViite(opsId, viiteId);
        if (result.getTyyppi() != tyyppi) {
            throw new BusinessRuleViolationException("sisalto-viite-tyyppi-vaara");
        }
        return result;
    }

    @Transactional
    private void clearChildrenAndParent(SisaltoViite parent, Set<SisaltoViite> viitteet) {
        for (SisaltoViite lapsi : parent.getLapset()) {
            viitteet.add(lapsi);
            clearChildrenAndParent(lapsi, viitteet);
        }
        parent.setVanhempi(null);
        parent.getLapset().clear();
    }

    @Transactional
    private void updatePoistetut(Koulutustoimija kt, Opetussuunnitelma ops, Set<SisaltoViite> viitteet) {
        for (SisaltoViite viite : viitteet) {
            poistetutService.lisaaPoistettu(kt.getId(), ops, viite);
            repository.delete(viite);
        }
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
                tekstiKappaleService.update(opsId, uusiTekstiKappale);
            } else {
                throw new BusinessRuleViolationException("Lainattua tekstikappaletta ei voida muokata");
            }
        }
    }

    // Kopioi viitehierarkian ja siirtää irroitetut paikoilleen
    // UUID parentin tunniste
    @Override
    @Transactional
    public SisaltoViite kopioiHierarkia(SisaltoViite original, Opetussuunnitelma owner, Map<SisaltoTyyppi, Set<String>> sisaltotyyppiIncludes, boolean copyTekstikappaleet) {
        if (sisaltotyyppiIncludes != null && CollectionUtils.isNotEmpty(sisaltotyyppiIncludes.get(original.getTyyppi()))) {
            if (original.getTyyppi().equals((SisaltoTyyppi.TUTKINNONOSA)) && !sisaltotyyppiIncludes.get(SisaltoTyyppi.TUTKINNONOSA).contains(original.getTosa().getKoodi())) {
                return null;
            }
        }

        SisaltoViite result = original.copy(false, copyTekstikappaleet);
        result.setOwner(owner);
        List<SisaltoViite> lapset = original.getLapset();

        if (lapset != null) {
            for (SisaltoViite lapsi : lapset) {
                SisaltoViite uusiLapsi = kopioiHierarkia(lapsi, owner, sisaltotyyppiIncludes, copyTekstikappaleet);
                if (uusiLapsi != null) {
                    uusiLapsi.setVanhempi(result);
                    result.getLapset().add(uusiLapsi);
                }
            }
        }

        return repository.save(result);
    }

    private void haeViitteet(SisaltoViite parent, Set<SisaltoViite> result) {
        Optional<SisaltoViite> viite = Optional.ofNullable(repository.findOne(parent.getId()));
        if (!viite.isPresent()) {
            throw new BusinessRuleViolationException("viite-puuttuu");
        }

        result.add(viite.get());

        for (SisaltoViite sv : parent.getLapset()) {
            haeViitteet(sv, result);
        }
    }

    private void haeViitteet(SisaltoViiteRakenneDto puu, Reference parent, Set<SisaltoViiteDto> result) {
        Optional<SisaltoViite> viiteOpt = Optional.ofNullable(repository.findOne(puu.getId()));

        if (!viiteOpt.isPresent()) {
            throw new BusinessRuleViolationException("viite-puuttuu");
        }

        SisaltoViite viite = viiteOpt.get();
        SisaltoViiteDto viiteDto = mapper.map(viite, SisaltoViiteDto.class);
        if (parent != null) {
            viiteDto.setVanhempi(parent);
        }
        result.add(viiteDto);

        for (SisaltoViiteRakenneDto sv : puu.getLapset()) {
            haeViitteet(sv, new Reference(puu.getId()), result);
        }
    }

    private void validateRakenne(SisaltoViite root, SisaltoViiteRakenneDto uusi, Opetussuunnitelma ops) {
        Set<SisaltoViite> vanhatViitteet = new HashSet<>();
        haeViitteet(root, vanhatViitteet);

        Set<SisaltoViiteDto> uudetViitteet = new HashSet<>();
        haeViitteet(uusi, uusi.getVanhempi(), uudetViitteet);

        Set<Long> uudetIds = uudetViitteet.stream()
                .map(SisaltoViiteDto::getId)
                .collect(Collectors.toSet());

        Map<Long, SisaltoViite> vanhatViitteetMap = vanhatViitteet.stream()
                .collect(Collectors.toMap(SisaltoViite::getId, Function.identity()));

        // Same
        boolean tutkinnonOsat = false;
        boolean suorituspolut = false;

        for (SisaltoViiteDto viiteDto : uudetViitteet) {
            if (viiteDto.getVanhempi() != null && !uudetIds.contains(viiteDto.getVanhempi().getIdLong())) {
                throw new BusinessRuleViolationException("vanhempi-puuttuu");
            }

            // liikkumaton
            /*
            Reference uusiParent = viiteDto.getVanhempi();
            SisaltoViite parent = vanhatViitteetMap.get(viiteDto.getId()).getVanhempi();
            if (viiteDto.isLiikkumaton()) {
                // Jos vanhempi on muuttunut (null on sallittu)
                if (uusiParent == null && parent != null
                        || uusiParent != null && parent == null
                        || uusiParent != null && !uusiParent.getIdLong().equals(parent.getId())) {
                    throw new BusinessRuleViolationException("liikkumaton-ei-saa-liikkua");
                }
            }
            */

            Reference uusiParent = viiteDto.getVanhempi();
            SisaltoViite parent = vanhatViitteetMap.get(viiteDto.getId()).getVanhempi();
            if (viiteDto.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                    && vanhatViitteetMap.get(uusiParent.getIdLong()).getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)) {
                throw new BusinessRuleViolationException("tutkinnonosa-parent-ei-voi-olla-tutkinnonosa");
            }


            if (viiteDto.getTyyppi() != null && viiteDto.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT)) {
                tutkinnonOsat = true;
            } else if (viiteDto.getTyyppi() != null && viiteDto.getTyyppi().equals(SisaltoTyyppi.SUORITUSPOLUT)) {
                suorituspolut = true;
            }
        }

        for (SisaltoViite vanhaViite : vanhatViitteet) {
            if (vanhaViite.isPakollinen() && !uudetIds.contains(vanhaViite.getId())) {
                throw new BusinessRuleViolationException("pakollinen-ei-saa-poistaa");
            }
        }

        if (ops.getPeruste() != null && KoulutusTyyppi.ammatilliset().contains(ops.getPeruste().getKoulutustyyppi())) {
            if (!tutkinnonOsat && (ops.getTyyppi() == OpsTyyppi.OPS || ops.getTyyppi() == OpsTyyppi.YLEINEN)) {
                throw new BusinessRuleViolationException("tosa-otsikko-puuttuu");
            }

            if (!suorituspolut && ops.getTyyppi() == OpsTyyppi.OPS) {
                throw new BusinessRuleViolationException("suorituspolut-puuttuvat");
            }
        }

        if (!Objects.equals(root.getId(), uusi.getId())) {
            throw new BusinessRuleViolationException("juurisolmua-ei-voi-vaihtaa");
        }
    }

    @Transactional
    private SisaltoViite updateViitteet(SisaltoViite parent, SisaltoViiteRakenneDto uusi, Set<SisaltoViite> viitteet) {
        SisaltoViite viite = repository.findOne(uusi.getId());

        if (viite == null || !viitteet.remove(viite)) {
            throw new BusinessRuleViolationException("viallinen-viitepuun-viite");
        }

        viite.setVanhempi(parent);

        List<SisaltoViite> lapset = viite.getLapset();
        lapset.clear();

        if (uusi.getLapset() != null) {
            lapset.addAll(uusi.getLapset().stream()
                    .map(el -> updateViitteet(viite, el, viitteet))
                    .collect(Collectors.toList()));
        }

        return repository.save(viite);
    }

    @Override
    @Transactional
    public void reorderSubTree(Long ktId, Long opsId, Long rootViiteId, SisaltoViiteRakenneDto uusi) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        SisaltoViite root = findViite(opsId, rootViiteId);

        // Validoidaan syötteitä
        if (root == null) {
            throw new BusinessRuleViolationException("juurisolmu-ei-olemassa");
        } else if (root.getVanhempi() != null) {
            throw new BusinessRuleViolationException("viite-ei-ole-juurisolmu");
        } else if (kt == null) {
            throw new BusinessRuleViolationException("koulutustoimija-ei-olemassa");
        } else if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelma-ei-olemassa");
        }

        // Validoidaan uusi rakenne
        validateRakenne(root, uusi, ops);

        repository.lock(root);

        // Poistetaan kaikki vanhojen viitteiden relaatiot ja lisätään ne settiin
        Set<SisaltoViite> vanhatViitteet = new HashSet<>();
        vanhatViitteet.add(root);
        clearChildrenAndParent(root, vanhatViitteet);

        // Lisää uudet relaatit juureen
        updateViitteet(null, uusi, vanhatViitteet);

        // Siirretään poistetut viitteet poistettuihin
        updatePoistetut(kt, ops, vanhatViitteet);
    }

    @Override
    public RevisionDto getLatestRevision(Long ktId, Long opsId, Long viiteId) {
        return mapper.map(repository.getLatestRevision(viiteId), RevisionDto.class);
    }

    @Override
    public SisaltoViiteDto getData(Long ktId, Long opsId, Long viiteId, Integer revId) {
        SisaltoViite viite = repository.findRevision(viiteId, revId);
        return mapper.map(viite, SisaltoViiteDto.class);
    }

    @Override
    public List<RevisionKayttajaDto> getRevisions(Long ktId, Long opsId, Long viiteId) {
        SisaltoViite viite = repository.findOne(viiteId);
        if (!Objects.equals(opsId, viite.getOwner().getId())) {
            throw new BusinessRuleViolationException("viitteen-taytyy-kuulua-opetussuunnitelmaan");
        }
        List<RevisionKayttajaDto> revisions = mapper.mapAsList(repository.getRevisions(viiteId), RevisionKayttajaDto.class);

        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(revisions.stream().map(RevisionKayttajaDto::getMuokkaajaOid).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));
        revisions.forEach(revision -> revision.setKayttajanTieto(kayttajatiedot.get(revision.getMuokkaajaOid())));

        return revisions;
    }

    @Override
    public void revertToVersion(Long ktId, Long opsId, Long viiteId, Integer versio) {
    }

    private List<SisaltoViite> rankTutkinnonOsat(List<SisaltoViite> viitteet) {
        viitteet.sort(Comparator.comparing(v -> v.getOwner().getLuotu()));
        viitteet.sort(Comparator.comparing(v -> v.getOwner().getTyyppi()));
        viitteet.sort(Comparator.comparing(v -> v.getOwner().getTila()));
        Collections.reverse(viitteet);
        return viitteet.stream()
                .filter(v -> v.getOwner().getTila() != Tila.POISTETTU)
                .collect(Collectors.toList());
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
        } else if (ktId != null && koodi.startsWith("tutkinnonosat_")) {
            Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
            List<SisaltoViite> tutkinnonOsaSisallot = repository.findAllTutkinnonOsatByKoodi(kt, koodi);
            return rankTutkinnonOsat(tutkinnonOsaSisallot);
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

    @Override
    public List<SuorituspolkuRakenneDto> getSuorituspolkurakenne(Long ktId, Long opsId) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        CachedPeruste cperuste = ops.getPeruste();
        if (cperuste != null) {
            PerusteKaikkiDto peruste = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class);
            List<SuorituspolkuRakenneDto> result = new ArrayList<>();
            if (peruste.getSuoritustavat() != null) {
                SuoritustapaLaajaDto suoritustapa = peruste.getSuoritustavat().stream().filter(st -> st.getSuoritustapakoodi()
                        .equals(Suoritustapakoodi.of(ops.getSuoritustapa()))).findFirst().get();
                List<SisaltoViiteDto> polut = getSuorituspolut(ktId, opsId, SisaltoViiteDto.class);
                for (SisaltoViiteDto viite : polut) {
                    SuorituspolkuRakenneDto rakenne = new SuorituspolkuRakenneDto();
                    SuorituspolkuDto polku = viite.getSuorituspolku();
                    Map<UUID, SuorituspolkuRiviDto> polkuMap = polku.getRivit().stream().collect(Collectors.toMap(SuorituspolkuRiviDto::getRakennemoduuli, Function.identity()));
                    result.add(luoSuorituspolkuRakenne(suoritustapa.getRakenne(), polkuMap));
                }
            }
            return result;
        }

        return Collections.emptyList();
    }

    private SuorituspolkuRakenneDto luoSuorituspolkuRakenne(RakenneModuuliDto rakenne, Map<UUID, SuorituspolkuRiviDto> polkuMap) {
        SuorituspolkuRakenneDto result = new SuorituspolkuRakenneDto();
        mapper.map(rakenne, result);
        result.setPaikallinenKuvaus(polkuMap.get(rakenne.getTunniste()));
        result.setOsat(rakenne.getOsat().stream()
                .filter(osa -> {
                    SuorituspolkuRiviDto paikallinen = polkuMap.get(osa.getTunniste());
                    boolean isValid = paikallinen == null || paikallinen.getPiilotettu() == null || !paikallinen.getPiilotettu();
                    return isValid;
                })
                .map(osa -> {
                    if (osa instanceof RakenneModuuliDto) {
                        return luoSuorituspolkuRakenne((RakenneModuuliDto) osa, polkuMap);
                    }
                    else {
                        SuorituspolkuOsaDto polunRakenneOsa = mapper.map(osa, SuorituspolkuOsaDto.class);
                        polunRakenneOsa.setPaikallinenKuvaus(polkuMap.get(osa.getTunniste()));
                        return polunRakenneOsa;
                    }
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public <T> List<T> getSuorituspolut(Long ktId, Long opsId, Class<T> aClass) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        List<SisaltoViite> polut = repository.findSuorituspolut(ops);
        return mapper.mapAsList(polut, aClass);
    }

    @Override
    public <T> List<T> getTutkinnonOsaViitteet(Long ktId, Long opsId, Class<T> aClass) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        List<SisaltoViite> tosat = repository.findTutkinnonosat(ops);
        return mapper.mapAsList(tosat, aClass);
    }

    @Override
    public <T> List<T> getTutkinnonOsat(Long ktId, Long opsId, Class<T> aClass) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        return repository.findTutkinnonosat(ops).stream()
                .map(SisaltoViite::getTosa)
                .map(tosa -> mapper.map(tosa, aClass))
                .collect(Collectors.toList());
    }

    @Override
    public List<SisaltoViiteDto> getSisaltoviitteet(Long ktId, Long opsId, SisaltoTyyppi tyyppi) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        return repository.findByTyyppi(ops, tyyppi).stream()
                .map(ko -> mapper.map(ko, SisaltoViiteDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SisaltoViiteDto> getOpetussuunnitelmanPohjanSisaltoviitteet(Long ktId, Long opsId, SisaltoTyyppi tyyppi) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops.getPohja() == null) {
            return null;
        }
        return getSisaltoviitteet(ops.getPohja().getKoulutustoimija().getId(), ops.getPohja().getId(), tyyppi);
    }

    @Override
    @Transactional(readOnly = false)
    public void copySisaltoViiteet(Long ktId, Long opsId, List<Long> viitteet) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        SisaltoViite root = repository.findOneRoot(ops);

        List<SisaltoViite> kopiot = viitteet.stream()
                .map(viiteId -> repository.findOne(viiteId))
                .filter(Objects::nonNull)
                .filter(viite -> SisaltoTyyppi.isCopyable(viite.getTyyppi()))
                .peek(viite -> {
                    // Koitetaan hakea perusteen tiedot viitteestä
                    if (ObjectUtils.isEmpty(viite.getPeruste())
                            && !ObjectUtils.isEmpty(viite.getOwner())
                            && !ObjectUtils.isEmpty(viite.getOwner().getPeruste())) {
                        viite.setPeruste(viite.getOwner().getPeruste());
                    }
                })
                .map(viite -> mapper.map(viite, SisaltoViiteDto.class))
                .map(viiteDto -> mapper.map(viiteDto, SisaltoViite.class))
                .map(viite -> SisaltoViite.copy(viite, false))
                .map(viite -> {
                    viite.setVanhempi(root);
                    viite.setValmis(false);
                    viite.setLiikkumaton(false);
                    viite.setPakollinen(false);
                    viite.setOwner(ops);
                    // Asetetaan oman opsin peruste jos ei tiedossa muita
                    if (ObjectUtils.isEmpty(viite.getPeruste())) {
                        viite.setPeruste(ops.getPeruste());
                    }
                    return repository.save(viite);
                })
                .collect(Collectors.toList());

        siirraViitteet(kopiot, root, SisaltoTyyppi.TUTKINNONOSA, SisaltoTyyppi.TUTKINNONOSAT);
        siirraViitteet(kopiot, root, SisaltoTyyppi.SUORITUSPOLKU, SisaltoTyyppi.SUORITUSPOLUT);

        root.getLapset().addAll(kopiot);
        repository.save(root);

        viitteet.stream()
                .map(viiteId -> repository.findOne(viiteId))
                .filter(Objects::nonNull)
                .filter(viite -> SisaltoTyyppi.isCopyable(viite.getTyyppi()))
                .map(viite -> viite.getOwner().getLiitteet().stream())
                .flatMap(liitteet -> liitteet)
                .forEach(liite -> ops.attachLiite(liite));

        opsRepository.save(ops);
    }

    private void siirraViitteet(List<SisaltoViite> viitteet, SisaltoViite root, SisaltoTyyppi siirrettavat, SisaltoTyyppi rootTyyppi) {
        Optional<SisaltoViite> rootViite = root.getLapset().stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(rootTyyppi)).findFirst();
        if (rootViite.isPresent()) {
            Set<SisaltoViite> filterViitteet = viitteet.stream().filter(sisaltoViite -> sisaltoViite.getTyyppi().equals(siirrettavat)).collect(Collectors.toSet());
            filterViitteet.forEach(viite -> {
                viite.setVanhempi(rootViite.get());
                repository.save(viite);
            });
            rootViite.get().getLapset().addAll(filterViitteet);
            repository.save(rootViite.get());
            viitteet.removeAll(filterViitteet);
        }
    }

}
