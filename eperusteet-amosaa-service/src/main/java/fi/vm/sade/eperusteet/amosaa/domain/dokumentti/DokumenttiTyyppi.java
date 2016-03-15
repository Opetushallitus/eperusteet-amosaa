package fi.vm.sade.eperusteet.amosaa.domain.dokumentti;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author isaul
 */
public enum DokumenttiTyyppi {

    OPS("ops"),
    YHTEISET("yhteiset");

    private final String tyyppi;

    DokumenttiTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }


    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static DokumenttiTyyppi of(String tila) {
        for (DokumenttiTyyppi t : values()) {
            if (t.tyyppi.equalsIgnoreCase(tila)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen dokumenttityyppi");
    }
}
