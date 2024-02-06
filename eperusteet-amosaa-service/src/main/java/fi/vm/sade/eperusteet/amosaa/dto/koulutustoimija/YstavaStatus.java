package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum YstavaStatus {
    YHTEISTYO("yhteistyo"), // Yhteistyö aloitettu kumpaankin suuntaan
    ODOTETAAN("odotetaan"); // Kysyjä odottaa hyväksyntää

    private final String tyyppi;

    private YstavaStatus(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static YstavaStatus of(String tila) {
        for (YstavaStatus s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }

    public boolean isOneOf(YstavaStatus[] tyypit) {
        for (YstavaStatus toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

}
