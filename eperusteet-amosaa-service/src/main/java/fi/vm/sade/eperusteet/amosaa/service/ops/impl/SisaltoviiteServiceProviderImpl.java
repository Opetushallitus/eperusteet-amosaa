package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import java.util.HashMap;
import java.util.Map;
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
    public SisaltoViiteToteutusService getSisaltoViiteToteutusService(SisaltoViite viite) {
        SisaltoViiteToteutusService result = sisaltoViiteToteutusServices.getOrDefault(viite.getTyyppi(), null);
        if (result == null) {
            throw new BusinessRuleViolationException("no implementation found");
        }
        return result;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        return getSisaltoViiteToteutusService(viite).updateSisaltoViite(viite, uusi);
    }

    @Override
    public void koodita(SisaltoViite viite) {
        try {
            getSisaltoViiteToteutusService(viite).koodita(viite);
        } catch (BusinessRuleViolationException e) {
            // skip
        }
    }
}
