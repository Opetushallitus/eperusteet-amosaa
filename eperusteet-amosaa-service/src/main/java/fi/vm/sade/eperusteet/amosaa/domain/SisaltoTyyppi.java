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
package fi.vm.sade.eperusteet.amosaa.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.EnumSet;


/**
 * @author nkala
 */
public enum SisaltoTyyppi {
    TEKSTIKAPPALE("tekstikappale"),
    TUTKINNONOSAT("tutkinnonosat"),
    TUTKINNONOSA("tutkinnonosa"),
    TOSARYHMA("tutkinnonosaryhma"),
    SUORITUSPOLUT("suorituspolut"),
    SUORITUSPOLKU("suorituspolku");

    private final String tyyppi;

    private SisaltoTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static SisaltoTyyppi of(String tila) {
        for (SisaltoTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }

    public boolean isOneOf(SisaltoTyyppi[] tyypit) {
        for (SisaltoTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCopyable(SisaltoTyyppi tyyppi) {
        return EnumSet.of(SUORITUSPOLKU, TUTKINNONOSA, TEKSTIKAPPALE).contains(tyyppi);
    }

    public static boolean salliLuonti(SisaltoTyyppi tyyppi) {
        return EnumSet.of(SUORITUSPOLKU, TUTKINNONOSA, TEKSTIKAPPALE).contains(tyyppi);
    }
}
