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
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
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

            // TODO: Validoi perustetta vasten
        }
    }

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
