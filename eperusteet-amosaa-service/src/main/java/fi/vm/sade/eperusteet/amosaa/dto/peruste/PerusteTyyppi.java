package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PerusteTyyppi {

    NORMAALI("normaali"),
    OPAS("opas"),
    AMOSAA_YHTEINEN("amosaayhteinen"),
    POHJA("pohja"),
    DIGITAALINEN_OSAAMINEN("digitaalinen_osaaminen");

    private final String value;

    private PerusteTyyppi(String tila) {
        this.value = tila;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static PerusteTyyppi of(String tila) {
        for (PerusteTyyppi s : values()) {
            if (s.value.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen PerusteTyyppi");
    }

    public boolean isOneOf(PerusteTyyppi... tyypit) {
        for (PerusteTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.value)) {
                return true;
            }
        }
        return false;
    }

}
