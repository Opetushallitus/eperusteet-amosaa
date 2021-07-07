package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Koulutuksenosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TuvaLaajaAlainenOsaaminen;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class LaajaAlainenOsaaminenSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.LAAJAALAINENOSAAMINEN;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        TuvaLaajaAlainenOsaaminen uusiTuvaLaajaAlainenOsaaminen = mapper.map(uusi.getTuvaLaajaAlainenOsaaminen(), TuvaLaajaAlainenOsaaminen.class);
        viite.setTuvaLaajaAlainenOsaaminen(uusiTuvaLaajaAlainenOsaaminen);
        return viite;
    }
}
