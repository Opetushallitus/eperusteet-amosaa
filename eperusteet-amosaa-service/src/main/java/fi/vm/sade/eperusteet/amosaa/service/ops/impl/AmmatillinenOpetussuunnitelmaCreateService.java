package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
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
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CacheManager cacheManager;

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
        opetussuunnitelma.setNimi(LokalisoituTeksti.of(opsDto.getNimi().getTeksti()));
        asetaPerusteenTutkinnonosatiedotPohjasta(pohja, opetussuunnitelma);
        kopioiPaikallisetTutkinnonosatPohjasta(pohja, opetussuunnitelma);
        kopioiPaikallisetTekstikappaleetPohjasta(pohja, opetussuunnitelma);

        return mapper.map(opetussuunnitelma, OpetussuunnitelmaBaseDto.class);
    }

    private void kopioiPaikallisetTekstikappaleetPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma opetussuunnitelma) {
        SisaltoViite rootSisaltoviite = opetussuunnitelma.getSisaltoviitteet().stream().filter(sv -> sv.getVanhempi() == null).findFirst().get();
        SisaltoViite pohjaRootSisaltoviite = pohja.getSisaltoviitteet().stream().filter(sv -> sv.getVanhempi() == null).findFirst().get();

        pohjaRootSisaltoviite.getLapset().stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TEKSTIKAPPALE))
                .forEach(sisaltoviite -> {
                    SisaltoViite uusiTk = kopioiTekstikappaleet(sisaltoviite,opetussuunnitelma);
                    uusiTk.setVanhempi(rootSisaltoviite);
                    rootSisaltoviite.getLapset().add(uusiTk);
                });
    }

    public SisaltoViite kopioiTekstikappaleet(SisaltoViite original, Opetussuunnitelma owner) {
        if (!original.getTyyppi().equals(SisaltoTyyppi.TEKSTIKAPPALE)) {
            return null;
        }

        SisaltoViite result = original.copy(false, SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI);
        result.setOwner(owner);
        List<SisaltoViite> lapset = original.getLapset();

        if (lapset != null) {
            for (SisaltoViite lapsi : lapset) {
                SisaltoViite uusiLapsi = kopioiTekstikappaleet(lapsi, owner);
                if (uusiLapsi != null) {
                    uusiLapsi.setVanhempi(result);
                    result.getLapset().add(uusiLapsi);
                }
            }
        }

        return sisaltoviiteRepository.save(result);
    }

    private void kopioiPaikallisetTutkinnonosatPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma opetussuunnitelma) {
        List<SisaltoViite> pohjastaKopioitavatPaikallisetTutkinnonOsat = CollectionUtil.treeToStream(pohja.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA))
                .collect(Collectors.toList());

        SisaltoViite tutkinnonosatViite = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findFirst().get();

        pohjastaKopioitavatPaikallisetTutkinnonOsat.forEach(kopioitavaPaikallinenTosa -> {
            SisaltoViite result = kopioitavaPaikallinenTosa.copy(false, SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI);
            result.setVanhempi(tutkinnonosatViite);
            tutkinnonosatViite.getLapset().add(result);
            result.setOwner(opetussuunnitelma);
            sisaltoviiteRepository.save(result);
        });
    }

    private void asetaPerusteenTutkinnonosatiedotPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma opetussuunnitelma) {
        cacheManager.getCache("perusteKaikki").evictIfPresent(opetussuunnitelma.getPeruste().getId());
        PerusteKaikkiDto perusteKaikkiDto = eperusteetService.getPerusteKaikki(opetussuunnitelma.getPeruste().getId());
        List<String> perusteenTosaKoodit = perusteKaikkiDto.getTutkinnonOsat().stream()
                .map(TutkinnonosaKaikkiDto::getKoodiUri)
                .collect(Collectors.toList());

        Map<String, SisaltoViite> pohjastaKopioitavatPerusteenTutkinnonOsat = CollectionUtil.treeToStream(pohja.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && perusteenTosaKoodit.contains(sisaltoviite.getTosa().getKoodi()))
                .collect(Collectors.toMap(viite -> viite.getTosa().getKoodi(), viite -> viite));

        CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)
                        && pohjastaKopioitavatPerusteenTutkinnonOsat.containsKey(sisaltoviite.getTosa().getKoodi()))
                .forEach(sisaltoviite -> {
                    SisaltoViite pohjanSisaltoviite = pohjastaKopioitavatPerusteenTutkinnonOsat.get(sisaltoviite.getTosa().getKoodi());
                    Map<String, OmaOsaAlue> pohjanOsaAlueet = pohjanSisaltoviite.getOsaAlueet().stream()
                            .filter(osaalue -> !osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))
                            .collect(Collectors.toMap(osaalue -> osaalue.getPerusteenOsaAlueId() + "" + osaalue.getTyyppi(), omaOsaalue -> omaOsaalue));
                    List<OmaOsaAlue> pohjanPaikallisetOsaAlueet = pohjanSisaltoviite.getOsaAlueet().stream()
                            .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))
                            .map(OmaOsaAlue::copy)
                            .collect(Collectors.toList());

                    sisaltoviite.getTosa().asetaPaikallisetMaaritykset(pohjanSisaltoviite.getTosa());
                    sisaltoviite.setTekstiKappale(pohjanSisaltoviite.getTekstiKappale().copy());
                    sisaltoviite.getOsaAlueet().addAll(pohjanPaikallisetOsaAlueet);
                    sisaltoviite.getOsaAlueet().stream()
                        .filter(osaalue -> pohjanOsaAlueet.containsKey(osaalue.getPerusteenOsaAlueId() + "" + osaalue.getTyyppi()))
                        .forEach(osaalue -> {
                            OmaOsaAlue pohjanOsaAlue = pohjanOsaAlueet.get(osaalue.getPerusteenOsaAlueId() + "" + osaalue.getTyyppi());
                            osaalue.asetaPaikallisetMaaritykset(pohjanOsaAlue);
                    });
                });
    }

    private Opetussuunnitelma asetaPerusteenTiedot(Koulutustoimija koulutustoimija, Opetussuunnitelma pohja, PerusteDto uusiPeruste) {
        Opetussuunnitelma ops = pohja.copy();
        ops.setPerusteDiaarinumero(uusiPeruste.getDiaarinumero());
        ops.setTila(Tila.LUONNOS);
        if (!ops.getSuoritustapa().equalsIgnoreCase(uusiPeruste.getSuoritustavat().stream().findFirst().get().getSuoritustapakoodi().value())) {
            ops.setSuoritustapa(uusiPeruste.getSuoritustavat().stream().findFirst().get().getSuoritustapakoodi().value());
        }
        ops = opetussuunnitelmaRepository.save(ops);
        SisaltoViite rootSisaltoviite = new SisaltoViite();
        rootSisaltoviite.setOwner(ops);
        rootSisaltoviite = sisaltoviiteRepository.save(rootSisaltoviite);
        opetussuunnitelmaService.setOpsCommon(koulutustoimija.getId(), ops, uusiPeruste, rootSisaltoviite);
        ops.getSisaltoviitteet().add(rootSisaltoviite);
        return ops;
    }

}
