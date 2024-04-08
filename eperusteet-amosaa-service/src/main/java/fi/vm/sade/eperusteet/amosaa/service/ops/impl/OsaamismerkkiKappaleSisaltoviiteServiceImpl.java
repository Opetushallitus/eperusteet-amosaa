package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OsaamismerkkiKappale;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
public class OsaamismerkkiKappaleSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.OSAAMISMERKKI;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getOsaamismerkkiKappale().getId(), viite.getOsaamismerkkiKappale().getId())) {
            throw new BusinessRuleViolationException("osaamismerkkikappaleen-viitetta-ei-voi-vaihtaa");
        }

        OsaamismerkkiKappale uusiOsaamismerkkiKappale = mapper.map(uusi.getOsaamismerkkiKappale(), OsaamismerkkiKappale.class);
        viite.setOsaamismerkkiKappale(uusiOsaamismerkkiKappale);
        return viite;
    }
}
