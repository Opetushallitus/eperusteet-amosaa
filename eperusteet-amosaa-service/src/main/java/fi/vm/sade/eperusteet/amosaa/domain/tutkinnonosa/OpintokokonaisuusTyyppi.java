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

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OpintokokonaisuusTyyppi {
    PERUSTEESTA("perusteesta"),
    OMA("oma");

    private final String tyyppi;

    OpintokokonaisuusTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static OpintokokonaisuusTyyppi of(String tyyppi) {
        for (OpintokokonaisuusTyyppi t : values()) {
            if (t.tyyppi.equalsIgnoreCase(tyyppi)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tyyppi + " ei ole kelvollinen tyyppi opetussuunnitelmalle");
    }

}
