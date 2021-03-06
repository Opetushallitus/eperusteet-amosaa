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

package fi.vm.sade.eperusteet.amosaa.domain.dokumentti;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author iSaul
 */
public enum DokumenttiTila {

    EI_OLE("ei_ole"),
    JONOSSA("jonossa"),
    LUODAAN("luodaan"),
    EPAONNISTUI("epaonnistui"),
    VALMIS("valmis");

    private final String tila;

    DokumenttiTila(String tila) {
        this.tila = tila;
    }


    @Override
    public String toString() {
        return tila;
    }

    @JsonCreator
    public static DokumenttiTila of(String tila) {
        for (DokumenttiTila t : values()) {
            if (t.tila.equalsIgnoreCase(tila)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen dokumenttitila");
    }
}
