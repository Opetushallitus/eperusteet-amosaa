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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nkala
 */
@Service
public class ValidointiServiceImpl implements ValidointiService {
    @Autowired
    private SisaltoviiteRepository svRepository;

    @Autowired
    private SisaltoViiteService svService;

    @Autowired
    private PerusteCacheService perusteCacheService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public Validointi validoi(Opetussuunnitelma ops) {
        Validointi validointi = new Validointi();
        validoi(validointi, ops);
        SisaltoViite root = svRepository.findOneRoot(ops);
        validoi(validointi, root, ops);
        return validointi;
    }

    private void validoiTutkinnonOsa(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        Tutkinnonosa tosa = viite.getTosa();
        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        if (tosa.getToteutukset().isEmpty()) {
            validointi.varoitus("tutkinnon-osalla-ei-toteutuksia", nimi);
        }

        if (tosa.getTyyppi() == TutkinnonosaTyyppi.OMA) {
            OmaTutkinnonosa oma = tosa.getOmatutkinnonosa();
            if (oma == null) {
                validointi.virhe("oma-tutkinnon-osa-ei-sisaltoa", nimi);
            }
            else {
                if (oma.getKoodi() == null || oma.getKoodi().isEmpty()) {
                    validointi.virhe("oma-tutkinnon-osa-ei-koodia", nimi);
                }

                long paikalliset = svRepository.findAllPaikallisetTutkinnonOsatByKoodi(ops.getKoulutustoimija(), oma.getKoodi()).stream()
                        .filter(sv -> sv.getOwner().getTila() == Tila.JULKAISTU || sv.getOwner().getId().equals(ops.getId()))
                        .count();
                if (paikalliset > 1) {
                    validointi.virhe("oma-tutkinnon-osa-koodi-jo-kaytossa", nimi);
                }

                // TODO: Tutkinnon osan sisällön tarkastus (Tehdään jos laatijat tekevät virheitä)
            }
        }
    }

    private class ValidointiHelper {
        public Validointi validointi;
        public Opetussuunnitelma ops;
        public SuoritustapaLaajaDto suoritustapa;
        public Suorituspolku sp;
        public Map<UUID, SuorituspolkuRivi> rivit;
        public Map<Long, TutkinnonOsaViiteSuppeaDto> tov;

        public ValidointiHelper(Validointi validointi, Opetussuunnitelma ops, SuoritustapaLaajaDto suoritustapa, Suorituspolku sp, Map<UUID, SuorituspolkuRivi> rivit, Map<Long, TutkinnonOsaViiteSuppeaDto> tov) {
            this.validointi = validointi;
            this.ops = ops;
            this.suoritustapa = suoritustapa;
            this.sp = sp;
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
            MuodostumisSaantoDto ms = ((RakenneModuuliDto)osa).getMuodostumisSaanto();
            if (ms != null) {
                int kokoMin = (ms.getKoko() != null && ms.getKoko().getMinimi() != null) ? ms.getKoko().getMinimi() : 0;
                int kokoMax = (ms.getKoko() != null && ms.getKoko().getMaksimi() != null) ? ms.getKoko().getMaksimi() : 0;
                int koko = kokoMax > kokoMin ? kokoMax : kokoMin;
                int laajuusMin = (ms.getLaajuus() != null && ms.getLaajuus().getMinimi() != null) ? ms.getLaajuus().getMinimi() : 0;
                int laajuusMax = (ms.getLaajuus() != null && ms.getLaajuus().getMaksimi() != null) ? ms.getLaajuus().getMaksimi() : 0;
                int laajuus = laajuusMax > laajuusMin ? laajuusMax : laajuusMin;
                return Pair.of(koko, new BigDecimal(laajuus));
            }
            // Ryhmälle ei ole määritelty osien laajuutta
            else {
                Pair<Integer, BigDecimal> ryhmanSisallonMuodostuminen = sisallonLaajuusJaKoko(ctx, (RakenneModuuliDto)osa);
                return ryhmanSisallonMuodostuminen;
            }
        }
        else if (osa instanceof RakenneOsaDto) {
            Long tovId = ((RakenneOsaDto)osa).getTutkinnonOsaViite();
            if (tovId != null) {
                TutkinnonOsaViiteSuppeaDto tov = ctx.tov.get(tovId);
                if (tov != null) {
                    return Pair.of(1, tov.getLaajuus());
                }
            }
        }
        return Pair.of(0, new BigDecimal(0));
    }

    private Pair<Integer, BigDecimal> sisallonLaajuusJaKoko(ValidointiHelper ctx, RakenneModuuliDto moduuli) {
        return moduuli.getOsat().stream()
            .filter(osa -> isUsedRakenneOsa(ctx, osa))
            .map(osa -> laskettuLaajuusJaKoko(ctx, osa))
            .reduce(Pair.of(0, new BigDecimal(0)), (acc, next) -> laajuusJaKokoReducer(acc, next));
    }

    /// Yksittäisen ryhmän tarkastus
    private void validoiSpRyhma(ValidointiHelper ctx, RakenneModuuliDto moduuli) {

        Pair<Integer, BigDecimal> sisallonKokoJaLaajuus = sisallonLaajuusJaKoko(ctx, moduuli);

        if (moduuli.getMuodostumisSaanto() != null) {
            try {
                Integer minimi = moduuli.getMuodostumisSaanto().getKoko().getMinimi();
                if (sisallonKokoJaLaajuus.getFirst() < minimi) {
                    ctx.validointi.virhe("virheellinen-rakenteen-osa", moduuli.getNimi());
                }
            }
            catch (NullPointerException ex) {}

            try {
                BigDecimal minimi = new BigDecimal(moduuli.getMuodostumisSaanto().getLaajuus().getMinimi());
                if (sisallonKokoJaLaajuus.getSecond().compareTo(minimi) == -1) {
                    ctx.validointi.virhe("virheellinen-rakenteen-osa", moduuli.getNimi());
                }
            }
            catch (NullPointerException ex) {}
        }
    }

    /// Suorituspolun validointi perusteen rakennepuuta vasten
    private void validoiSpRakenne(ValidointiHelper ctx, RakenneModuuliDto moduuli) {
        SuorituspolkuRivi rivi = ctx.rivit.get(moduuli.getTunniste());
        // Jos ryhmä on piilotettu, sitä ei huomioida
        if (rivi != null && (rivi.getPiilotettu() != null && rivi.getPiilotettu())) {
            return;
        }

        validoiSpRyhma(ctx, moduuli);

        moduuli.getOsat().stream()
                .filter(osa -> osa instanceof RakenneModuuliDto)
                .map(osa -> (RakenneModuuliDto)osa)
                .filter(osa -> osa.getRooli() == RakenneModuuliRooli.NORMAALI || osa.getRooli() == RakenneModuuliRooli.OSAAMISALA)
                .forEach(osa -> {
                    validoiSpRakenne(ctx, osa);
                });
    }

    /// Suorituspolun validointi
    private void validoiSuorituspolku(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        Suorituspolku sp = viite.getSuorituspolku();
        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        if (sp != null && sp.getRivit() != null) {
            for (SuorituspolkuRivi rivi : sp.getRivit()) {
                LokalisoituTeksti.validoi(validointi, ops, rivi.getKuvaus());
                for (String koodi : rivi.getKoodit()) {
                    if (svService.getCountByKoodi(ops.getKoulutustoimija().getId(), koodi) == 0) {
                        String[] koodiSplit = koodi.split("_");
                        validointi.varoitus("suorituspolku-koodi-ei-toteutusta", nimi, LokalisoituTeksti.of(Kieli.FI, koodiSplit.length > 0 ? koodiSplit[koodiSplit.length - 1] : koodi));
                    }
                }
            }

            CachedPeruste perusteInfo = ops.getPeruste();
            SuoritustapaLaajaDto suoritustapa = perusteCacheService.getSuoritustapa(ops, perusteInfo.getId());
            Map<UUID, SuorituspolkuRivi> rivit = sp.getRivit().stream()
                    .collect(Collectors.toMap(SuorituspolkuRivi::getRakennemoduuli, Function.identity()));
            Map<Long, TutkinnonOsaViiteSuppeaDto> tov = suoritustapa.getTutkinnonOsat().stream()
                    .collect(Collectors.toMap(TutkinnonOsaViiteSuppeaDto::getId, Function.identity()));
            ValidointiHelper ctx = new ValidointiHelper(validointi, ops, suoritustapa, sp, rivit, tov);
            validoiSpRakenne(ctx, suoritustapa.getRakenne());
        }
    }

    /// Validoidaan sisältöviite
    private void validoi(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        if (viite == null || viite.getLapset() == null) {
            return;
        }

        LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        switch (viite.getTyyppi()) {
            case TUTKINNONOSA:
                validoiTutkinnonOsa(validointi, viite, ops);
            case SUORITUSPOLKU:
                validoiSuorituspolku(validointi, viite, ops);
            default:
                break;
        }

        for (SisaltoViite lapsi : viite.getLapset()) {
            if (lapsi.isPakollinen()) {
                if (lapsi.getTekstiKappale() != null) {
                    LokalisoituTeksti.validoi(validointi, ops, lapsi.getTekstiKappale().getNimi(), nimi);
                }
                else {
                    validointi.virhe("tekstikappaleella-ei-lainkaan-sisaltoa", nimi);
                }
            }
            validoi(validointi, lapsi, ops);
        }
    }

    static public void validoi(Validointi validointi, Opetussuunnitelma ops) {
        LokalisoituTeksti.validoi(validointi, ops, ops.getNimi());
        LokalisoituTeksti.validoi(validointi, ops, ops.getKuvaus());

        if (ops.getJulkaisukielet().isEmpty()) {
            validointi.virhe("julkaisukielet-ei-maaritelty", ops.getNimi());
        }

        if (ops.getTyyppi() == OpsTyyppi.OPS || ops.getTyyppi() == OpsTyyppi.YLEINEN) {
            if (ops.getHyvaksyja() == null || ops.getHyvaksyja().isEmpty()) {
                validointi.virhe("hyvaksyjaa-ei-maaritelty", ops.getNimi());
            }

            if (ops.getPaatosnumero()== null || ops.getPaatosnumero().isEmpty()) {
                validointi.virhe("paatosnumeroa-ei-maaritelty", ops.getNimi());
            }

            if (ops.getPaatosnumero() == null) {
                validointi.virhe("paatospaivamaaraa-ei-maaritelty", ops.getNimi());
            }
        }
    }
}
