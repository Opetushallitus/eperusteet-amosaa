package fi.vm.sade.eperusteet.amosaa.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.EnumSet;

public enum SisaltoTyyppi {
    TEKSTIKAPPALE("tekstikappale"),
    TUTKINNONOSAT("tutkinnonosat"),

    TUTKINNONOSA("tutkinnonosa"),
    TOSARYHMA("tutkinnonosaryhma"),
    SUORITUSPOLUT("suorituspolut"),
    SUORITUSPOLKU("suorituspolku"),
    OSASUORITUSPOLKU("osasuorituspolku"),
    OPINTOKOKONAISUUS("opintokokonaisuus"),
    KOULUTUKSENOSA("koulutuksenosa"),
    KOULUTUKSENOSAT("koulutuksenosat"),
    LAAJAALAINENOSAAMINEN("laajaalainenosaaminen"),
    KOTO_KIELITAITOTASO("koto_kielitaitotaso"),
    KOTO_OPINTO("koto_opinto"),
    KOTO_LAAJAALAINENOSAAMINEN("koto_laajaalainenosaaminen"),

    // Linkki toiseen minkä läpi voi vain lukea
    LINKKI("linkki");

    private final String tyyppi;

    private SisaltoTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static SisaltoTyyppi of(String tila) {
        for (SisaltoTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen SisaltoTyyppi");
    }

    public boolean isOneOf(SisaltoTyyppi... tyypit) {
        for (SisaltoTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLinkable(SisaltoTyyppi tyyppi) {
        return EnumSet.of(TUTKINNONOSA).contains(tyyppi);
    }

    public static boolean isCopyable(SisaltoTyyppi tyyppi) {
        return EnumSet.of(SUORITUSPOLKU, TUTKINNONOSA, TEKSTIKAPPALE, LINKKI).contains(tyyppi);
    }

    public static boolean salliLuonti(SisaltoTyyppi tyyppi) {
        return EnumSet.of(SUORITUSPOLKU, OSASUORITUSPOLKU, TUTKINNONOSA, TEKSTIKAPPALE, OPINTOKOKONAISUUS).contains(tyyppi);
    }

    public static boolean isSuorituspolku(SisaltoTyyppi tyyppi) {
        return EnumSet.of(SUORITUSPOLKU, OSASUORITUSPOLKU).contains(tyyppi);
    }

    public boolean isAmmatillinenTyyppi() {
        return isOneOf(TUTKINNONOSAT, SUORITUSPOLUT, SUORITUSPOLKU, TUTKINNONOSA, TEKSTIKAPPALE, TOSARYHMA, OSASUORITUSPOLKU);
    }

    public boolean isVstTyyppi() {
        return isOneOf(TEKSTIKAPPALE, OPINTOKOKONAISUUS);
    }

    public boolean isTuvaTyyppi() {
        return isOneOf(TEKSTIKAPPALE, KOULUTUKSENOSA, KOULUTUKSENOSAT, LAAJAALAINENOSAAMINEN);
    }
}
