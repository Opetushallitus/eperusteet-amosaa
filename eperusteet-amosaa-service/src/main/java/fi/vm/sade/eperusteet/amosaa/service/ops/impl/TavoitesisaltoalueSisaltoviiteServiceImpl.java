package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tavoitesisaltoalue;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TavoitesisaltoalueSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.TAVOITESISALTOALUE;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        Tavoitesisaltoalue uusiTavoitesisaltoalue = mapper.map(uusi.getTavoitesisaltoalue(), Tavoitesisaltoalue.class);
        viite.setTavoitesisaltoalue(uusiTavoitesisaltoalue);
        return viite;
    }
}
