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

package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author nkala
 */

public enum OpsTyyppi {
    POHJA("pohja"), // OPH:n yhteisien osien pohja
    OPS("ops"), // Normaali opetussuunnitelma
    YLEINEN("yleinen"), // Opetussuunnitelmien kesken jaettava opetussuunnitelma
    YHTEINEN("yhteinen"); // Koulutustoimijan yhteiset osat

    private final String tyyppi;

    OpsTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }


    @Override
    public String toString() {
        return tyyppi;
    }

    public boolean isOneOf(OpsTyyppi... tyypit) {
        for (OpsTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

    @JsonCreator
    public static OpsTyyppi of(String tila) {
        for (OpsTyyppi t : values()) {
            if (t.tyyppi.equalsIgnoreCase(tila)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tyyppi opetussuunnitelmalle");
    }
}
