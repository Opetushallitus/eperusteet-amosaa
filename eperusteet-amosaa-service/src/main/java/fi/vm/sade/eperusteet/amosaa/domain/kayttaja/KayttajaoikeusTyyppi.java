package fi.vm.sade.eperusteet.amosaa.domain.kayttaja;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum KayttajaoikeusTyyppi {
    ESTETTY("estetty"), // Käytetään ystäväorganisaatioiden jäsenten kanssa
    LUKU("luku"),
    MUOKKAUS("muokkaus"),
    LISAYS("lisays"),
    POISTO("poisto"),
    HALLINTA("hallinta");

    private final String tyyppi;

    private KayttajaoikeusTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static KayttajaoikeusTyyppi of(String tila) {
        for (KayttajaoikeusTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }

    public boolean isAtLeast(KayttajaoikeusTyyppi tyyppi) {
        if (tyyppi == this) {
            return true;
        }

        switch (tyyppi) {
            case LUKU:
                return this.isOneOf(HALLINTA, POISTO, LISAYS, MUOKKAUS);
            case MUOKKAUS:
                return this.isOneOf(HALLINTA, POISTO, LISAYS);
            case LISAYS:
                return this.isOneOf(HALLINTA, POISTO);
            case POISTO:
                return this.isOneOf(HALLINTA);
            default:
                break;
        }
        return false;
    }

    public boolean isOneOf(KayttajaoikeusTyyppi... tyypit) {
        for (KayttajaoikeusTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }
}
