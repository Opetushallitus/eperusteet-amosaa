package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;

import java.util.Optional;

public interface SisaltoviiteServiceProvider {

    Optional<SisaltoViiteToteutusService> getSisaltoViiteToteutusService(SisaltoViite viite);

    SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi);

    void koodita(SisaltoViite viite);

    boolean validoiKoodi(SisaltoViite viite);

}
