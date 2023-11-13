package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoUriArvo;
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

import fi.vm.sade.eperusteet.amosaa.service.util.impl.KoodistoClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class OpintokokonaisuusSisaltoviiteServiceImpl implements SisaltoViiteToteutusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoodistoClient koodistoClient;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    public SisaltoTyyppi getSisaltoTyyppi() {
        return SisaltoTyyppi.OPINTOKOKONAISUUS;
    }

    @Override
    public SisaltoViite updateSisaltoViite(SisaltoViite viite, SisaltoViiteDto uusi) {
        if (!Objects.equals(uusi.getOpintokokonaisuus().getId(), viite.getOpintokokonaisuus().getId())) {
            throw new BusinessRuleViolationException("opintokokonaisuuden-viitetta-ei-voi-vaihtaa");
        }

        Opintokokonaisuus uusiOpintokokonaisuus = mapper.map(uusi.getOpintokokonaisuus(), Opintokokonaisuus.class);

        // opintokokonaisuuden koodia ei saa vaihtaa koski-linkityksien vuoksi
        if (viite.getOpintokokonaisuus().getKoodi() != null) {
            uusiOpintokokonaisuus.setKoodi(viite.getOpintokokonaisuus().getKoodi());
        }

        viite.setOpintokokonaisuus(uusiOpintokokonaisuus);
        return viite;
    }

    @Override
    public void koodita(SisaltoViite viite) {
        SisaltoViiteDto sisaltoViiteDto = mapper.map(viite, SisaltoViiteDto.class);

        kooditaOpintokokonaisuus(sisaltoViiteDto);
        kooditaTavoitteet(sisaltoViiteDto);

        Opintokokonaisuus uusiOpintokokonaisuus = mapper.map(sisaltoViiteDto.getOpintokokonaisuus(), Opintokokonaisuus.class);
        viite.setOpintokokonaisuus(uusiOpintokokonaisuus);

        sisaltoviiteRepository.save(viite);
    }

    @Override
    public boolean validoiKoodi(SisaltoViite viite) {
        SisaltoViiteDto sisaltoViiteDto = mapper.map(viite, SisaltoViiteDto.class);
        return sisaltoViiteDto.getOpintokokonaisuus().getTavoitteet()
                .stream().filter(tavoite -> StringUtils.isEmpty(tavoite.getTavoiteKoodi()))
                .allMatch(tavoite -> KoodistoClientImpl.validoiKooditettava(tavoite.getTavoite()));
    }

    private void kooditaOpintokokonaisuus(SisaltoViiteDto sisaltoViiteDto) {
        if (StringUtils.isEmpty(sisaltoViiteDto.getOpintokokonaisuus().getKoodi()) && sisaltoViiteDto.getTekstiKappale().getNimi() != null) {
            KoodistoKoodiDto lisattyKoodi = koodistoClient.addKoodiNimella(KoodistoUriArvo.OPINTOKOKONAISUUDET, sisaltoViiteDto.getTekstiKappale().getNimi());
            if (lisattyKoodi == null) {
                log.error("Koodin lisääminen epäonnistui: {} {}", KoodistoUriArvo.OPINTOKOKONAISUUDET, sisaltoViiteDto.getTekstiKappale().getNimi());
            }
            sisaltoViiteDto.getOpintokokonaisuus().setKoodi(lisattyKoodi.getKoodiUri());
        }
    }

    private void kooditaTavoitteet(SisaltoViiteDto sisaltoViiteDto) {
        List<OpintokokonaisuusTavoiteDto> tallentamattomat = sisaltoViiteDto.getOpintokokonaisuus().getTavoitteet()
                .stream().filter(tavoite -> StringUtils.isEmpty(tavoite.getTavoiteKoodi()))
                .collect(Collectors.toList());

        Stack<Long> koodiStack = new Stack<>();
        koodiStack.addAll(koodistoClient.nextKoodiId(KoodistoUriArvo.OPINTOKOKONAISUUS_TAVOITTEET, tallentamattomat.size()));

        for (OpintokokonaisuusTavoiteDto tavoite : sisaltoViiteDto.getOpintokokonaisuus().getTavoitteet()) {
            if (StringUtils.isEmpty(tavoite.getTavoiteKoodi())) {
                KoodistoKoodiDto lisattyKoodi = koodistoClient.addKoodiNimella(KoodistoUriArvo.OPINTOKOKONAISUUS_TAVOITTEET, tavoite.getTavoite(), koodiStack.pop());
                if (lisattyKoodi == null) {
                    log.error("Koodin lisääminen epäonnistui: {} {}", KoodistoUriArvo.OPINTOKOKONAISUUS_TAVOITTEET, tavoite.getTavoite().getTekstit());
                    continue;
                }
                tavoite.setTavoiteKoodi(lisattyKoodi.getKoodiUri());
                tavoite.setTavoite(null);
            }
        }
    }
}
