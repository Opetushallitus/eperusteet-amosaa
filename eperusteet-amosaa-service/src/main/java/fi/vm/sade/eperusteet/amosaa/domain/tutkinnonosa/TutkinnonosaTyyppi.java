package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TutkinnonosaTyyppi {
    PERUSTEESTA("perusteesta"), // Perusteen kautta käytettävä tutkinnon osa
    OMA("oma"), // Itse toteutettu tutkinnon osa
    VIERAS("vieras"), // Jokin muualta tuotu tutkinnon osa
    YHTEINEN("yhteinen"); // Viite jonkin muun opetussuunnitelman tutkinnon osaan

    private final String tyyppi;

    TutkinnonosaTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static TutkinnonosaTyyppi of(String tila) {
        for (TutkinnonosaTyyppi t : values()) {
            if (t.tyyppi.equalsIgnoreCase(tila)) {
                return t;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tyyppi opetussuunnitelmalle");
    }

}
