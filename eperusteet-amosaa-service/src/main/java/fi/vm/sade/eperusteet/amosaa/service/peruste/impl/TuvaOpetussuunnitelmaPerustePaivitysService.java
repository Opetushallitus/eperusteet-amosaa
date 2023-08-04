package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KoulutuksenosanPaikallinenTarkennus;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaSisaltoCreateService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TuvaOpetussuunnitelmaPerustePaivitysService extends MuutOpetussuunnitelmaPerustePaivitysService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private OpetussuunnitelmaSisaltoCreateService opetussuunnitelmaSisaltoCreateService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
    }

    @Override
    public void paivitaOpetussuunnitelma(Long opetussuunnitelmaId, PerusteKaikkiDto perusteKaikkiDto) {
        super.paivitaOpetussuunnitelma(opetussuunnitelmaId, perusteKaikkiDto);
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opetussuunnitelmaId);

        if (!opetussuunnitelma.getTyyppi().equals(OpsTyyppi.OPS)) {
            return;
        }

        SisaltoViite koulutuksenosatViite = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(viite -> viite.getTyyppi().equals(SisaltoTyyppi.KOULUTUKSENOSAT))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("toteutussuunnitelmalta-puutteellinen"));

        koulutuksenosatViite.setLapset(CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(viite -> viite.getTyyppi().equals(SisaltoTyyppi.KOULUTUKSENOSA))
                .peek(koulutuksenosaViite -> {
                    koulutuksenosaViite.setVanhempi(koulutuksenosatViite);
                    if (koulutuksenosaViite.getKoulutuksenosa().getPaikallinenTarkennus() == null) {
                        koulutuksenosaViite.getKoulutuksenosa().setPaikallinenTarkennus(new KoulutuksenosanPaikallinenTarkennus());
                    }
                }).collect(Collectors.toList()));
    }

}
