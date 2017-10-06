package fi.vm.sade.eperusteet.amosaa.domain.dokumentti;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author iSaul
 */
public enum DokumenttiEdistyminen {

    META("meta"),
    TUNTEMATON("tuntematon"),
    TEKSTIKAPPALEET("tekstikappaleet"),
    KUVAT("kuvat"),
    VIITTEET("viitteet"),
    TYYLIT("tyylit");

    private final String edistyminen;

    DokumenttiEdistyminen(String tila) {
        this.edistyminen = tila;
    }

    @Override
    public String toString() {
        return edistyminen;
    }

    @JsonCreator
    public static DokumenttiEdistyminen of(String tila) {
        for (DokumenttiEdistyminen t : values()) {
            if (t.edistyminen.equalsIgnoreCase(tila)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen dokumenttiedistyminen");
    }
}

