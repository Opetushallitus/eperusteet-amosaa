package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.impl.OpetussuunnitelmaSisaltoCreateUtil;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AmmatillinenOpetussuunnitelmaPerustePaivitysService implements OpetussuunnitelmaPerustePaivitysService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.ammatilliset());
    }

    @Override
    public void paivitaOpetussuunnitelma(Long opetussuunnitelmaId, PerusteKaikkiDto perusteKaikkiDto) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opetussuunnitelmaId);

        List<SisaltoViite> opetussuunnitelmanTutkinnonosaViitteet = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA) && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA))
                .collect(Collectors.toList());

        List<String> opetussuunnitelmanTosaKoodit = opetussuunnitelmanTutkinnonosaViitteet.stream()
                .map(sisaltoviite -> sisaltoviite.getTosa().getKoodi()).collect(Collectors.toList());

        List<String> perusteenTosaKoodit = perusteKaikkiDto.getTutkinnonOsat().stream()
                .map(TutkinnonosaKaikkiDto::getKoodiUri)
                .collect(Collectors.toList());

        List<TutkinnonosaKaikkiDto> lisattavatTutkinnonOsat = perusteKaikkiDto.getTutkinnonOsat().stream()
                .filter(tosa -> !opetussuunnitelmanTosaKoodit.contains(tosa.getKoodiUri()))
                .collect(Collectors.toList());

        List<SisaltoViite> poistettavatTutkinnonosat = opetussuunnitelmanTutkinnonosaViitteet.stream()
                .filter(viite -> !perusteenTosaKoodit.contains(viite.getTosa().getKoodi()))
                .collect(Collectors.toList());

        SisaltoViite tutkinnonosatViite = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findFirst().get();

        for (TutkinnonosaKaikkiDto tosa : lisattavatTutkinnonOsat) {
            SisaltoViite uusi = OpetussuunnitelmaSisaltoCreateUtil.perusteenTutkinnonosaToSisaltoviite(tutkinnonosatViite, tosa);
            sisaltoviiteRepository.save(uusi);
        }

        CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .forEach(viite -> {
                    if (!CollectionUtils.isEmpty(viite.getLapset())) {
                        List<Long> lapsiIdt = viite.getLapset().stream().map(SisaltoViite::getId).collect(Collectors.toList());
                        poistettavatTutkinnonosat.stream().filter(poistettava -> lapsiIdt.contains(poistettava.getId()))
                                .forEach(poistettava -> {
                                    viite.getLapset().remove(poistettava);
                                });
                    }
                });
    }
}
