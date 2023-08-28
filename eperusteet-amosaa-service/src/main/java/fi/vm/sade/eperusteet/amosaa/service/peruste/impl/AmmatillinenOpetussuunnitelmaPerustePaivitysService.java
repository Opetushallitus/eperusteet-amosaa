package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKokonaanDto;
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
import java.util.Map;
import java.util.Objects;
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

        Map<String, SisaltoViite> opetussuunnitelmanTutkinnonosaViitteet = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA) && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA))
                .collect(Collectors.toMap(sisaltoviite -> sisaltoviite.getTosa().getKoodi(), sisaltoviite -> sisaltoviite));

        Map<String, TutkinnonosaKaikkiDto> perusteenTosaKoodit = perusteKaikkiDto.getTutkinnonOsat().stream()
                .collect(Collectors.toMap(TutkinnonosaKaikkiDto::getKoodiUri, tosa -> tosa));

        SisaltoViite tutkinnonosatViite = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findFirst().get();

        perusteKaikkiDto.getTutkinnonOsat().forEach(tutkinnonosaKaikkiDto -> {
            if (!opetussuunnitelmanTutkinnonosaViitteet.containsKey(tutkinnonosaKaikkiDto.getKoodiUri())) {
                SisaltoViite uusi = OpetussuunnitelmaSisaltoCreateUtil.perusteenTutkinnonosaToSisaltoviite(tutkinnonosatViite, tutkinnonosaKaikkiDto);
                sisaltoviiteRepository.save(uusi);
            } else {
                paivitaTutkinnonOsaPerusteenTiedoilla(opetussuunnitelmanTutkinnonosaViitteet.get(tutkinnonosaKaikkiDto.getKoodiUri()), tutkinnonosaKaikkiDto);
            }
        });

        poistaTutkinnonOsat(opetussuunnitelma, opetussuunnitelmanTutkinnonosaViitteet, perusteenTosaKoodit);
    }

    private void poistaTutkinnonOsat(Opetussuunnitelma opetussuunnitelma, Map<String, SisaltoViite> opetussuunnitelmanTutkinnonosaViitteet, Map<String, TutkinnonosaKaikkiDto> perusteenTosaKoodit) {
        List<SisaltoViite> poistettavatTutkinnonosat = opetussuunnitelmanTutkinnonosaViitteet.values().stream()
                .filter(viite -> !perusteenTosaKoodit.containsKey(viite.getTosa().getKoodi()))
                .collect(Collectors.toList());

        CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .forEach(viite -> {
                    if (!CollectionUtils.isEmpty(viite.getLapset())) {
                        List<Long> lapsiIdt = viite.getLapset().stream().map(SisaltoViite::getId).collect(Collectors.toList());
                        poistettavatTutkinnonosat.stream().filter(poistettava -> lapsiIdt.contains(poistettava.getId()))
                                .forEach(poistettava -> {
                                    viite.getLapset().remove(poistettava);
                                    sisaltoviiteRepository.save(viite);
                                    sisaltoviiteRepository.delete(poistettava);
                                });
                    }
                });
    }

    private void paivitaTutkinnonOsaPerusteenTiedoilla(SisaltoViite sisaltoViite, TutkinnonosaKaikkiDto tutkinnonosaKaikkiDto) {
        List<Long> perusteenOsaAlueIdt = tutkinnonosaKaikkiDto.getOsaAlueet().stream().map(OsaAlueDto::getId).collect(Collectors.toList());
        List<Long> opsinPerusteenOsaAlueIdt = sisaltoViite.getOsaAlueet().stream()
                .map(OmaOsaAlue::getPerusteenOsaAlueId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        sisaltoViite.getOsaAlueet().removeAll(sisaltoViite.getOsaAlueet().stream()
                .filter(osaalue -> !osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))
                .filter(omaosaalue -> !perusteenOsaAlueIdt.contains(omaosaalue.getPerusteenOsaAlueId()))
                .collect(Collectors.toList()));
        tutkinnonosaKaikkiDto.getOsaAlueet().stream()
                .filter(osaalue -> !opsinPerusteenOsaAlueIdt.contains(osaalue.getId()))
                .forEach(osaalue -> OpetussuunnitelmaSisaltoCreateUtil.addPerusteenOsaAlueToSisaltoViite(sisaltoViite, osaalue));
    }
}
