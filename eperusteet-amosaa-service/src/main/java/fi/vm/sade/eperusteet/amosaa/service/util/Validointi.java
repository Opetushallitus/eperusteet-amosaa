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
package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.ValidointiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 *
 * @author nkala
 */
@Getter
public class Validointi {
    @Getter
    static public class Virhe {
        private String syy;
        private Map<Kieli, String> nimi;

        Virhe(String syy) {
            this.syy = syy;
            this.nimi = null;
        }

        Virhe(String syy, LokalisoituTeksti t) {
            this.syy = syy;
            this.nimi = t.getTeksti();
        }

        Virhe(String syy, LokalisoituTekstiDto t) {
            this.syy = syy;
            this.nimi = t.getTeksti();
        }
    }

    private List<Virhe> virheet = new ArrayList<>();
    private List<Virhe> varoitukset = new ArrayList<>();
    private List<Virhe> huomiot = new ArrayList<>();

    public Validointi virhe(String syy) {
        virheet.add(new Virhe(syy));
        return this;
    }

    public Validointi virhe(String syy, LokalisoituTekstiDto... args) {
        for (LokalisoituTekstiDto arg : args) {
            virheet.add(new Virhe(syy, arg));
        }
        return this;
    }

    public Validointi virhe(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            virheet.add(new Virhe(syy, arg));
        }
        return this;
    }

    public Validointi varoitus(String syy) {
        varoitukset.add(new Virhe(syy));
        return this;
    }

    public Validointi varoitus(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            varoitukset.add(new Virhe(syy, arg));
        }
        return this;
    }

    public Validointi huomio(String syy) {
        huomiot.add(new Virhe(syy));
        return this;
    }

    public Validointi huomio(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            huomiot.add(new Virhe(syy, arg));
        }
        return this;
    }

    public void tuomitse() {
        if (!virheet.isEmpty()) {
            throw new ValidointiException(this);
        }
    }
}
