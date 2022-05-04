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
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nkala
 */
public enum KoulutusTyyppi {
    PERUSTUTKINTO("koulutustyyppi_1"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    TELMA("koulutustyyppi_5"),
    VALMA("koulutustyyppi_18"),
    VAPAASIVISTYSTYO("koulutustyyppi_10"),
    VAPAASIVISTYSTYO_VANHA("koulutustyyppi_30"), // depricated. Olemassa cached datan vuoksi.
    VAPAASIVISTYSTYOLUKUTAITO("koulutustyyppi_35"),
    TUTKINTOONVALMENTAVA("koulutustyyppi_40");

    private final String tyyppi;

    KoulutusTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static KoulutusTyyppi of(String koulutustyyppi) {
        for (KoulutusTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(koulutustyyppi) || s.name().equals(koulutustyyppi)) {
                return s;
            }
        }
        throw new IllegalArgumentException(koulutustyyppi + " ei ole kelvollinen koulutustyyppi");
    }

    public boolean isOneOf(KoulutusTyyppi... tyypit) {
        for (KoulutusTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValmaTelma() {
        return isOneOf(VALMA, TELMA);
    }

    public boolean isAmmatillinen() {
        return isOneOf(AMMATTITUTKINTO, ERIKOISAMMATTITUTKINTO, PERUSTUTKINTO);
    }

    public boolean isVST() {
        return isOneOf(VAPAASIVISTYSTYO, VAPAASIVISTYSTYOLUKUTAITO);
    }

    public boolean isTuva() {
        return isOneOf(TUTKINTOONVALMENTAVA);
    }

    public static List<KoulutusTyyppi> ammatilliset() {
        return Arrays.asList(PERUSTUTKINTO, AMMATTITUTKINTO, ERIKOISAMMATTITUTKINTO, TELMA, VALMA);
    }
    
}
