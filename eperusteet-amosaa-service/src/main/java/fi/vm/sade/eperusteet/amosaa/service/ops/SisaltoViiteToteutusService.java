package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;

public interface SisaltoViiteToteutusService {

    SisaltoTyyppi getSisaltoTyyppi();

    SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi);

    default void koodita(SisaltoViite viite) {
    }
}
