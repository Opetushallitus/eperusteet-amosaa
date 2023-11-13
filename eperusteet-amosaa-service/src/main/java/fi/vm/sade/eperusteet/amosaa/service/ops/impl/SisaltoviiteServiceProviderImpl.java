package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SisaltoviiteServiceProviderImpl implements SisaltoviiteServiceProvider {

    private Map<SisaltoTyyppi, SisaltoViiteToteutusService> sisaltoViiteToteutusServices;

    @Autowired
    public void setToteutukset(Set<SisaltoViiteToteutusService> toteutukset) {
        sisaltoViiteToteutusServices = new HashMap<>();
        for (SisaltoViiteToteutusService toteutus : toteutukset) {
            sisaltoViiteToteutusServices.put(toteutus.getSisaltoTyyppi(), toteutus);
        }
    }

    @Override
    public Optional<SisaltoViiteToteutusService> getSisaltoViiteToteutusService(SisaltoViite viite) {
        return Optional.ofNullable(sisaltoViiteToteutusServices.getOrDefault(viite.getTyyppi(), null));
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        return getSisaltoViiteToteutusService(viite)
                .map(sisaltoViiteToteutusService -> sisaltoViiteToteutusService.updateSisaltoViite(viite, uusi))
                .orElse(null);

    }

    @Override
    public void koodita(SisaltoViite viite) {
        getSisaltoViiteToteutusService(viite)
                .ifPresent(sisaltoViiteToteutusService -> sisaltoViiteToteutusService.koodita(viite));
    }

    @Override
    public boolean validoiKoodi(SisaltoViite viite) {
        return getSisaltoViiteToteutusService(viite)
                .map(sisaltoViiteToteutusService -> sisaltoViiteToteutusService.validoiKoodi(viite)).orElse(true);
    }
}
