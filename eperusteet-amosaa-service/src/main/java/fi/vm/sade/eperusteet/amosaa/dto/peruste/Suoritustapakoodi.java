package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Suoritustapakoodi {

    OPS("ops"),
    NAYTTO("naytto"),
    REFORMI("reformi"),
    PERUSOPETUS("perusopetus"),
    LISAOPETUS("lisaopetus"),
    VARHAISKASVATUS("varhaiskasvatus"),
    ESIOPETUS("esiopetus"),
    LUKIOKOULUTUS("lukiokoulutus");

    private final String koodi;

    Suoritustapakoodi(String koodi) {
        this.koodi = koodi;
    }

    @Override
    public String toString() {
        return koodi;
    }

    @JsonCreator
    public static Suoritustapakoodi of(String koodi) {
        for (Suoritustapakoodi s : values()) {
            if (s.koodi.equalsIgnoreCase(koodi)) {
                return s;
            }
        }
        throw new IllegalArgumentException(koodi + " ei ole kelvollinen suoritustapakoodi");
    }

    @JsonValue
    public String value() {
        return name();
    }
}
