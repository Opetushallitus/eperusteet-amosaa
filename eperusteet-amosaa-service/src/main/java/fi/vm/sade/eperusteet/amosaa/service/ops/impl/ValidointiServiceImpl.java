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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.ValidationCategory;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;

/**
 * @author nkala
 */
@Slf4j
@Service
public class ValidointiServiceImpl implements ValidointiService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private PerusteCacheService perusteCacheService;

    @Autowired
    private DtoMapper mapper;

    private ObjectMapper objMapper;

    @PostConstruct
    protected void init() {
        objMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objMapper.registerModule(module);
        objMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    public List<Validointi> validoi(Opetussuunnitelma ops) {
        Validointi opsValidointi = new Validointi(ValidationCategory.OPETUSSUUNNITELMA);
        validoiTiedot(opsValidointi, ops);
        SisaltoViite root = sisaltoviiteRepository.findOneRoot(ops);

        Validointi sisaltoValidointi = new Validointi(ValidationCategory.SISALTO);
        validoi(sisaltoValidointi, root, ops);
        return Arrays.asList(opsValidointi, sisaltoValidointi);
    }

    @Override
    public List<String> tutkinnonOsanValidointivirheet(Long opsId, Long viiteId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        SisaltoViite viite = sisaltoviiteRepository.findOne(viiteId);
        if (ops == null || viite == null || !ops.equals(viite.getOwner())) {
            throw new BusinessRuleViolationException("virhe");
        }

        Validointi validointi = new Validointi(ValidationCategory.SISALTO);
        validoiTutkinnonOsa(validointi, viite, ops);
        return validointi.getVirheet().stream().map(Validointi.Virhe::getKuvaus).collect(Collectors.toList());
    }

    private void validoiTutkinnonOsa(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        Tutkinnonosa tosa = viite.getTosa();
        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        CachedPeruste cperuste = ops.getPeruste();
        String koulutustyppi;
        try {
            JsonNode node = objMapper.readTree(cperuste.getPeruste());
            JsonNode koulutustyyppi = node.get("koulutustyyppi");
            koulutustyppi = koulutustyyppi.asText();
        } catch (IOException ex) {
            log.error("perusteen-parsinta-epaonnistui" + cperuste.getId().toString());
            log.error("perusteen-parsinta-epaonnistui" + ex.getMessage());
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        if (tosa.getToteutukset().isEmpty()) {
            if (KoulutusTyyppi.of(koulutustyppi).isValmaTelma()) {
                validointi.huomautukset("koulutuksen-osalla-ei-toteutuksia", NavigationNodeDto.of(viite));
            } else {
                validointi.huomautukset("tutkinnon-osalla-ei-toteutuksia", NavigationNodeDto.of(viite));
            }
        } else {
            tosa.getToteutukset().forEach(toteutus -> {
                if (toteutus.getOtsikko() == null) {
                    validointi.virhe("toteutuksen-otsikko-ei-maaritelty", NavigationNodeDto.of(viite));
                }
            });
        }

        if (tosa.getTyyppi() == TutkinnonosaTyyppi.OMA) {
            OmaTutkinnonosa oma = tosa.getOmatutkinnonosa();
            if (oma == null) {
                if (KoulutusTyyppi.of(koulutustyppi).isValmaTelma()) {
                    validointi.virhe("oma-koulutuksen-osa-ei-sisaltoa", NavigationNodeDto.of(viite));
                } else {
                    validointi.virhe("oma-tutkinnon-osa-ei-sisaltoa", NavigationNodeDto.of(viite));
                }
            } else {
                try {
                    Integer arvo = Integer.valueOf(oma.getKoodi());
                    if (arvo < 1000 || arvo > 999999999) {
                        validointi.virhe("oma-tutkinnon-osa-virheellinen-koodi", NavigationNodeDto.of(viite));
                    }
                }
                catch (NumberFormatException ex) {
                    validointi.virhe("oma-tutkinnon-osa-virheellinen-koodi", NavigationNodeDto.of(viite));
                }

                long paikalliset = sisaltoviiteRepository.findAllPaikallisetTutkinnonOsatByKoodi(ops.getKoulutustoimija(), oma.getKoodi()).stream()
                        .filter(sv -> sv.getOwner().getId().equals(ops.getId()))
                        .count();
                if (paikalliset > 1) {
                    if (KoulutusTyyppi.of(koulutustyppi).isValmaTelma()) {
                        validointi.virhe("oma-koulutuksen-osa-koodi-jo-kaytossa", NavigationNodeDto.of(viite));
                    } else {
                        validointi.virhe("oma-tutkinnon-osa-koodi-jo-kaytossa", NavigationNodeDto.of(viite));
                    }
                }

                // TODO: Tutkinnon osan sisällön tarkastus (Tehdään jos laatijat tekevät virheitä)
            }
        }
    }

    private class ValidointiHelper {
        public Validointi validointi;
        public Opetussuunnitelma ops;
        public SuoritustapaLaajaDto suoritustapa;
        public SisaltoViite viite;
        public Map<UUID, SuorituspolkuRivi> rivit;
        public Map<Long, TutkinnonOsaViiteSuppeaDto> tov;

        public LokalisoituTekstiDto getViiteNimi() {
            return Optional.ofNullable(this.viite)
                    .map(SisaltoViite::getTekstiKappale)
                    .map(TekstiKappale::getNimi)
                    .map(nimi -> mapper.map(nimi, LokalisoituTekstiDto.class))
                    .orElse(null);
        }

        public ValidointiHelper(Validointi validointi, Opetussuunnitelma ops, SuoritustapaLaajaDto suoritustapa, SisaltoViite viite, Map<UUID, SuorituspolkuRivi> rivit, Map<Long, TutkinnonOsaViiteSuppeaDto> tov) {
            this.validointi = validointi;
            this.ops = ops;
            this.suoritustapa = suoritustapa;
            this.viite = viite;
            this.rivit = rivit;
            this.tov = tov;
        }
    }

    private boolean isUsedRakenneOsa(ValidointiHelper ctx, AbstractRakenneOsaDto osa) {
        UUID tunniste = osa.getTunniste();
        SuorituspolkuRivi rivi = ctx.rivit.get(tunniste);
        return rivi == null || rivi.getPiilotettu() == null || !rivi.getPiilotettu();
    }

    private Pair<Integer, BigDecimal> laajuusJaKokoReducer(Pair<Integer, BigDecimal> acc, Pair<Integer, BigDecimal> next) {
        BigDecimal second = next.getSecond() != null ? next.getSecond() : new BigDecimal(0L);
        return Pair.of(
                acc.getFirst() + next.getFirst(),
                acc.getSecond().add(second));
    }

    private Pair<Integer, BigDecimal> laskettuLaajuusJaKoko(ValidointiHelper ctx, AbstractRakenneOsaDto osa) {
        if (osa instanceof RakenneModuuliDto) {
            MuodostumisSaantoDto ms = ((RakenneModuuliDto) osa).getMuodostumisSaanto();
            if (ms != null) {
                Pair<Integer, BigDecimal> muodostumisSaanotLaskettuLaajuus = muodostumisSaanotLaskettuLaajuus(ms);
                if (muodostumisSaanotLaskettuLaajuus.getFirst() > 0 || muodostumisSaanotLaskettuLaajuus.getSecond().compareTo(BigDecimal.ZERO) > 0) {
                    return muodostumisSaanotLaskettuLaajuus;
                }
            }

            // Ryhmälle ei ole määritelty osien laajuutta
            Pair<Integer, BigDecimal> ryhmanSisallonMuodostuminen = sisallonLaajuusJaKoko(ctx, (RakenneModuuliDto) osa);
            return ryhmanSisallonMuodostuminen;
        }
        else if (osa instanceof RakenneOsaDto) {
            Long tovId = ((RakenneOsaDto) osa).getTutkinnonOsaViite();
            if (tovId != null) {
                TutkinnonOsaViiteSuppeaDto tov = ctx.tov.get(tovId);
                if (tov != null) {
                    if (tov.getLaajuusMaksimi() != null && tov.getLaajuus().compareTo(tov.getLaajuusMaksimi()) <= 0) {
                        return Pair.of(1, tov.getLaajuusMaksimi());
                    } else {
                        return Pair.of(1, tov.getLaajuus());
                    }
                }
            }
        }
        return Pair.of(0, new BigDecimal(0));
    }

    private Pair<Integer, BigDecimal> muodostumisSaanotLaskettuLaajuus(MuodostumisSaantoDto ms) {
        int kokoMin = (ms.getKoko() != null && ms.getKoko().getMinimi() != null) ? ms.getKoko().getMinimi() : 0;
        int kokoMax = (ms.getKoko() != null && ms.getKoko().getMaksimi() != null) ? ms.getKoko().getMaksimi() : 0;
        int koko = Math.max(kokoMax, kokoMin);
        int laajuusMin = (ms.getLaajuus() != null && ms.getLaajuus().getMinimi() != null) ? ms.getLaajuus().getMinimi() : 0;
        int laajuusMax = (ms.getLaajuus() != null && ms.getLaajuus().getMaksimi() != null) ? ms.getLaajuus().getMaksimi() : 0;
        int laajuus = Math.max(laajuusMax, laajuusMin);
        return Pair.of(koko, new BigDecimal(laajuus));
    }

    private Pair<Integer, BigDecimal> sisallonLaajuusJaKoko(ValidointiHelper ctx, RakenneModuuliDto moduuli) {
        return moduuli.getOsat().stream()
                .filter(osa -> isUsedRakenneOsa(ctx, osa))
                .map(osa -> laskettuLaajuusJaKoko(ctx, osa))
                .reduce(Pair.of(0, new BigDecimal(0)), this::laajuusJaKokoReducer);
    }

    // Yksittäisen ryhmän tarkastus
    private void validoiSpRyhma(ValidointiHelper ctx, RakenneModuuliDto moduuli, SisaltoViite viite) {

        Pair<Integer, BigDecimal> sisallonKokoJaLaajuus = sisallonLaajuusJaKoko(ctx, moduuli);

        if (moduuli.getMuodostumisSaanto() != null) {
            try {
                Integer minimi = moduuli.getMuodostumisSaanto().getKoko().getMinimi();
                if (sisallonKokoJaLaajuus.getFirst() < minimi) {
                    ctx.validointi.virhe("virheellinen-rakenteen-osa", NavigationNodeDto.of(viite), moduuli.getNimi().getTeksti());
                }
            } catch (NullPointerException ex) {
            }

            try {
                BigDecimal minimi = new BigDecimal(moduuli.getMuodostumisSaanto().getLaajuus().getMinimi());
                if (sisallonKokoJaLaajuus.getSecond().compareTo(minimi) < 0) {
                    ctx.validointi.virhe("virheellinen-rakenteen-osa", NavigationNodeDto.of(viite), moduuli.getNimi().getTeksti());
                }
            } catch (NullPointerException ex) {
            }
        }
    }

    // Suorituspolun validointi perusteen rakennepuuta vasten
    private void validoiSpRakenne(ValidointiHelper ctx, RakenneModuuliDto moduuli, SisaltoViite viite) {
        SuorituspolkuRivi rivi = ctx.rivit.get(moduuli.getTunniste());
        // Jos ryhmä on piilotettu, sitä ei huomioida
        if (rivi != null && (rivi.getPiilotettu() != null && rivi.getPiilotettu())) {
            return;
        }

        validoiSpRyhma(ctx, moduuli, viite);

        moduuli.getOsat().stream()
                .filter(osa -> osa instanceof RakenneModuuliDto)
                .map(osa -> (RakenneModuuliDto) osa)
                .filter(osa -> osa.getRooli() == RakenneModuuliRooli.NORMAALI || osa.getRooli() == RakenneModuuliRooli.OSAAMISALA)
                .forEach(osa -> validoiSpRakenne(ctx, osa, viite));
    }

    // Suorituspolun validointi
    private void validoiSuorituspolku(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        Suorituspolku sp = viite.getSuorituspolku();
        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        if (sp != null && sp.getRivit() != null) {
            for (SuorituspolkuRivi rivi : sp.getRivit()) {
                if (rivi.getPiilotettu() == null || !rivi.getPiilotettu()) {
                    LokalisoituTeksti.validoi(validointi, ops, rivi.getKuvaus(), NavigationNodeDto.of(viite));
                }
                for (String koodi : rivi.getKoodit()) {
                    if (sisaltoViiteService.getCountByKoodi(ops.getKoulutustoimija().getId(), koodi) == 0) {
                        String[] koodiSplit = koodi.split("_");
                        validointi.huomautukset("suorituspolku-koodi-ei-toteutusta", NavigationNodeDto.of(viite), Optional.ofNullable(nimi).map(LokalisoituTeksti::getTeksti).orElse(null));
                    }
                }
            }

            CachedPeruste perusteInfo = ops.getPeruste();
            SuoritustapaLaajaDto suoritustapa = perusteCacheService.getSuoritustapa(ops, perusteInfo.getId());
            Map<UUID, SuorituspolkuRivi> rivit = sp.getRivit().stream()
                    .collect(Collectors.toMap(SuorituspolkuRivi::getRakennemoduuli, Function.identity()));
            Map<Long, TutkinnonOsaViiteSuppeaDto> tov = suoritustapa.getTutkinnonOsat().stream()
                    .collect(Collectors.toMap(TutkinnonOsaViiteSuppeaDto::getId, Function.identity()));
            ValidointiHelper ctx = new ValidointiHelper(validointi, ops, suoritustapa, viite, rivit, tov);
            validoiSpRakenne(ctx, suoritustapa.getRakenne(), viite);
        }
    }

    // Validoidaan sisältöviite
    private void validoi(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        if (viite == null || viite.getLapset() == null) {
            return;
        }

        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        switch (viite.getTyyppi()) {
            case TUTKINNONOSA:
                validoiTutkinnonOsa(validointi, viite, ops);
            //case OSASUORITUSPOLKU: Osasuorituspolkua ei validoida
            case SUORITUSPOLKU:
                validoiSuorituspolku(validointi, viite, ops);
            default:
                break;
        }

        for (SisaltoViite lapsi : viite.getLapset()) {
            if (lapsi == null) {
                log.info("Sisältöviitteen validointi NPE: ", viite.getId());
                continue;
            }
            if (lapsi.isPakollinen()) {
                if (lapsi.getTekstiKappale() != null) {
                    LokalisoituTeksti.validoi(validointi, ops, lapsi.getTekstiKappale().getNimi(), NavigationNodeDto.of(lapsi));
                } else {
                    validointi.virhe("tekstikappaleella-ei-lainkaan-sisaltoa", NavigationNodeDto.of(viite));
                }
            }
            validoi(validointi, lapsi, ops);
        }
    }

    static public void validoiTiedot(Validointi validointi, Opetussuunnitelma ops) {
        LokalisoituTeksti.validoi(validointi, ops, ops.getNimi(), NavigationNodeDto.of(NavigationType.tiedot, LokalisoituTekstiDto.of(ops.getNimi().getTeksti())));

        if (ops.getJulkaisukielet().isEmpty()) {
            validointi.virhe("julkaisukielet-ei-maaritelty", NavigationNodeDto.of(NavigationType.tiedot), ops.getNimi().getTeksti());
        }

        if (ops.getTyyppi() == OpsTyyppi.OPS || ops.getTyyppi() == OpsTyyppi.YLEINEN) {
            if (ObjectUtils.isEmpty(ops.getHyvaksyja())) {
                validointi.virhe("hyvaksyjaa-ei-maaritelty", NavigationNodeDto.of(NavigationType.tiedot), ops.getNimi().getTeksti());
            }

            if (ObjectUtils.isEmpty(ops.getPaatosnumero())) {
                validointi.virhe("paatosnumeroa-ei-maaritelty", NavigationNodeDto.of(NavigationType.tiedot), ops.getNimi().getTeksti());
            }

            if (ObjectUtils.isEmpty(ops.getPaatospaivamaara())) {
                validointi.virhe("paatospaivamaaraa-ei-maaritelty", NavigationNodeDto.of(NavigationType.tiedot), ops.getNimi().getTeksti());
            }
        }
    }
}
