package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Koulutuksenosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class KoulutuksenosaSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoodistoClient koodistoClient;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.KOULUTUKSENOSA;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        Koulutuksenosa uusiKoulutuksenosa = mapper.map(uusi.getKoulutuksenosa(), Koulutuksenosa.class);
        viite.setKoulutuksenosa(uusiKoulutuksenosa);
        return viite;
    }
}
