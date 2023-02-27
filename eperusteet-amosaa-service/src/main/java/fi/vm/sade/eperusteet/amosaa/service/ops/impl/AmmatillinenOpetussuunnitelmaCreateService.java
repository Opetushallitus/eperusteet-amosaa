package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AmmatillinenOpetussuunnitelmaCreateService implements OpetussuunnitelmaCreateService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.ammatilliset());
    }

    @Override
    public OpetussuunnitelmaBaseDto create(Koulutustoimija koulutustoimija, OpetussuunnitelmaLuontiDto opsDto) {
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(opsDto.getOpsId());
        PerusteDto uusiPeruste = eperusteetService.getKoulutuskoodillaKorvaavaPeruste(koulutustoimija.getId(), pohja.getId());
        if (uusiPeruste == null) {
            return null;
        }

        Opetussuunnitelma opetussuunnitelma = asetaPerusteenTiedot(koulutustoimija, pohja, uusiPeruste);
        asetaTutkinnonosatiedotPohjasta(pohja, opetussuunnitelma);
        kopioiTutkinnonosatPohjasta(pohja, opetussuunnitelma);

        return mapper.map(opetussuunnitelma, OpetussuunnitelmaBaseDto.class);
    }

    private void kopioiTutkinnonosatPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma opetussuunnitelma) {
        List<SisaltoViite> pohjastaKopioitavatPaikallisetTutkinnonOsat = CollectionUtil.treeToStream(pohja.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA))
                .collect(Collectors.toList());

        SisaltoViite tutkinnonosatViite = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findFirst().get();

        pohjastaKopioitavatPaikallisetTutkinnonOsat.forEach(kopioitavaPaikallinenTosa -> {
            SisaltoViite result = kopioitavaPaikallinenTosa.copy(false, true);
            result.setVanhempi(tutkinnonosatViite);
            tutkinnonosatViite.getLapset().add(result);
            result.setOwner(opetussuunnitelma);
            sisaltoviiteRepository.save(result);
        });
    }

    private void asetaTutkinnonosatiedotPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma opetussuunnitelma) {
        PerusteKaikkiDto perusteKaikkiDto = eperusteetService.getPerusteKaikki(opetussuunnitelma.getPeruste().getId());
        List<String> perusteenTosaKoodit = perusteKaikkiDto.getTutkinnonOsat().stream()
                .map(TutkinnonosaKaikkiDto::getKoodiUri)
                .collect(Collectors.toList());

        Map<String, Tutkinnonosa> pohjastaKopioitavatPerusteenTutkinnonOsat = CollectionUtil.treeToStream(pohja.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && perusteenTosaKoodit.contains(sisaltoviite.getTosa().getKoodi()))
                .collect(Collectors.toMap(viite -> viite.getTosa().getKoodi(), SisaltoViite::getTosa));


        CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset).collect(Collectors.toSet()).stream()
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && pohjastaKopioitavatPerusteenTutkinnonOsat.containsKey(sisaltoviite.getTosa().getKoodi()))
                .forEach(sisaltoviite -> {
                    sisaltoviite.getTosa().asetaPaikallisetMaaritykset(pohjastaKopioitavatPerusteenTutkinnonOsat.get(sisaltoviite.getTosa().getKoodi()));
                });
    }

    private Opetussuunnitelma asetaPerusteenTiedot(Koulutustoimija koulutustoimija, Opetussuunnitelma pohja, PerusteDto uusiPeruste) {
        Opetussuunnitelma ops = pohja.copy();
        ops.setTila(Tila.LUONNOS);
        ops = opetussuunnitelmaRepository.save(ops);
        SisaltoViite rootSisaltoviite = new SisaltoViite();
        rootSisaltoviite.setOwner(ops);
        rootSisaltoviite = sisaltoviiteRepository.save(rootSisaltoviite);
        opetussuunnitelmaService.setOpsCommon(koulutustoimija.getId(), ops, uusiPeruste, rootSisaltoviite);
        ops.getSisaltoviitteet().add(rootSisaltoviite);
        return ops;
    }

}
