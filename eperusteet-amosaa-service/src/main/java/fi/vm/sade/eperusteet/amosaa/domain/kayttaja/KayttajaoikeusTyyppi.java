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

package fi.vm.sade.eperusteet.amosaa.domain.kayttaja;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author nkala
 */
public enum KayttajaoikeusTyyppi {
    ESTETTY("estetty"), // Käytetään ystäväorganisaatioiden jäsenten kanssa
    LUKU("luku"),
    MUOKKAUS("muokkaus"),
    LISAYS("lisays"),
    POISTO("poisto"),
    HALLINTA("hallinta");

    private final String tyyppi;

    private KayttajaoikeusTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static KayttajaoikeusTyyppi of(String tila) {
        for (KayttajaoikeusTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }

    public boolean isAtLeast(KayttajaoikeusTyyppi tyyppi) {
        if (tyyppi == this) {
            return true;
        }

        switch (tyyppi) {
            case LUKU:
                return this.isOneOf(HALLINTA, POISTO, LISAYS, MUOKKAUS);
            case MUOKKAUS:
                return this.isOneOf(HALLINTA, POISTO, LISAYS);
            case LISAYS:
                return this.isOneOf(HALLINTA, POISTO);
            case POISTO:
                return this.isOneOf(HALLINTA);
            default:
                break;
        }
        return false;
    }

    public boolean isOneOf(KayttajaoikeusTyyppi... tyypit) {
        for (KayttajaoikeusTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }
}
