package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteToteutusService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OpintokokonaisuusSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoodistoClient koodistoClient;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.OPINTOKOKONAISUUS;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getOpintokokonaisuus().getId(), viite.getOpintokokonaisuus().getId())) {
            throw new BusinessRuleViolationException("opintokokonaisuuden-viitetta-ei-voi-vaihtaa");
        }

        List<OpintokokonaisuusTavoiteDto> tallentamattomat = uusi.getOpintokokonaisuus().getTavoitteet()
                .stream().filter(tavoite -> tavoite.getTavoiteKoodi() == null || tavoite.getTavoiteKoodi().isEmpty())
                .collect(Collectors.toList());

        Stack<Long> koodiStack = new Stack<>();
        koodiStack.addAll(koodistoClient.nextKoodiId("opintokokonaisuustavoitteet", tallentamattomat.size()));

        for (OpintokokonaisuusTavoiteDto tavoite : uusi.getOpintokokonaisuus().getTavoitteet()) {
            if (tavoite.getTavoiteKoodi() == null || tavoite.getTavoiteKoodi().isEmpty()) {
                KoodistoKoodiDto lisattyKoodi = koodistoClient.addKoodiNimella("opintokokonaisuustavoitteet", tavoite.getTavoite(), koodiStack.pop());
                if (lisattyKoodi == null) {
                    log.error("Koodin lisääminen epäonnistui: {} {}", "opintokokonaisuustavoitteet", tavoite.getTavoite().getTekstit());
                    continue;
                }
                tavoite.setTavoiteKoodi(lisattyKoodi.getKoodiUri());
            }
        }
        Opintokokonaisuus uusiOpintokokonaisuus = mapper.map(uusi.getOpintokokonaisuus(), Opintokokonaisuus.class);
        viite.setOpintokokonaisuus(uusiOpintokokonaisuus);
        return viite;
    }
}
