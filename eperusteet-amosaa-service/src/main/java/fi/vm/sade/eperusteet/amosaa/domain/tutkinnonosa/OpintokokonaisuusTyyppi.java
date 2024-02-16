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
