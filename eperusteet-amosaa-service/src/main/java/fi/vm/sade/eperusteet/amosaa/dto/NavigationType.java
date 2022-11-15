package fi.vm.sade.eperusteet.amosaa.dto;

public enum NavigationType {
    root,
    tiedot,
    viite,
    liitteet, liite,
    peruste,
    opetussuunnitelma,
    tekstikappale,
    tutkinnonosat,
    tutkinnonosat_pakolliset,
    tutkinnonosat_paikalliset,
    tutkinnonosat_tuodut,
    valmatelmaKoulutuksenosat,
    tutkinnonosa,
    tosaryhma,
    suorituspolut,
    suorituspolku,
    osasuorituspolku,
    opintokokonaisuus,
    koulutuksenosa,
    koulutuksenosat,
    laajaalainenosaaminen,
    koto_kielitaitotaso,
    koto_opinto,
    koto_laajaalainenosaaminen,
    opetussuunnitelma_rakenne,
    linkki;

    public static NavigationType of(String type) {
        for (NavigationType t : values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException(type + " ei ole kelvollinen tila");
    }
}
