package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OpsTyyppi {
    POHJA("pohja"), // OPH:n yhteisien osien pohja
    OPS("ops"), // Normaali opetussuunnitelma
    OPSPOHJA("opspohja"), // Normaali opetussuunnitelman pohja
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
