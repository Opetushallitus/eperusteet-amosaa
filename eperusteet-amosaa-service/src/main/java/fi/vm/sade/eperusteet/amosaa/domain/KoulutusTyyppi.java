package fi.vm.sade.eperusteet.amosaa.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.List;

public enum KoulutusTyyppi {
    PERUSTUTKINTO("koulutustyyppi_1"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    TELMA("koulutustyyppi_5"),
    VALMA("koulutustyyppi_18"),
    VAPAASIVISTYSTYO("koulutustyyppi_10"),
    MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS("koulutustyyppi_30"),
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
