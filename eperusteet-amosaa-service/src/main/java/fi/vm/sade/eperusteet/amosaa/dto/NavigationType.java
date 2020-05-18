package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;

public enum NavigationType {
    root,
    tiedot,
    viite,
    liitteet, liite,
    peruste,
    tekstikappale,
    tutkinnonosat,
    tutkinnonosa,
    tosaryhma,
    suorituspolut,
    suorituspolku,
    osasuorituspolku;

    public static NavigationType of(String type) {
        for (NavigationType t : values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException(type + " ei ole kelvollinen tila");
    }
}
