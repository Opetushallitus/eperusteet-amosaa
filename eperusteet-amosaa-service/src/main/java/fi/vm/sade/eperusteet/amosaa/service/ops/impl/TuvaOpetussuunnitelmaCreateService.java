package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KoulutuksenosanPaikallinenTarkennus;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaPohjaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TuvaOpetussuunnitelmaCreateService implements OpetussuunnitelmaCreateService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private SisaltoviiteRepository tkvRepository;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private SisaltoViiteService tkvService;

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private TekstiKappaleRepository tkRepository;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
    }

    @Override
    public OpetussuunnitelmaBaseDto create(Koulutustoimija koulutustoimija, OpetussuunnitelmaLuontiDto opsDto) {

        Opetussuunnitelma pohja = repository.findOne(opsDto.getOpsId());

        if (pohja == null) {
            throw new BusinessRuleViolationException("ei-oikeutta-opetussuunnitelmaan");
        }

        SisaltoViite sisaltoRoot = tkvRepository.findOneRoot(pohja);
        Opetussuunnitelma ops = pohja.copy();
        ops.setTyyppi(OpsTyyppi.OPS);
        ops.changeKoulutustoimija(koulutustoimija);
        ops.setNimi(mapper.map(opsDto.getNimi(), LokalisoituTeksti.class));
        ops.changeKoulutustoimija(koulutustoimija);

        ops = repository.save(ops);

        SisaltoViite root = kopioiHierarkia(sisaltoRoot, ops, pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA) ? SisaltoViite.TekstiHierarkiaKopiointiToiminto.POHJAVIITE : SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI);

        if (pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA)) {
            List<SisaltoViite> koulutuksenosatViitteet = koulutuksenosat(root);
            lisaaPaikallisetTarkennukset(koulutuksenosatViitteet);
            SisaltoViite koulutuksenosatViite = createKoulutuksenosatViite();
            koulutuksenosatViite.setOwner(ops);
            koulutuksenosatViite.setVanhempi(root);
            koulutuksenosatViite.setLapset(koulutuksenosatViitteet);
            koulutuksenosatViitteet.forEach(koulutuksenosa -> {
                koulutuksenosa.setVanhempi(koulutuksenosatViite);
            });

            root.getLapset().removeAll(koulutuksenosatViitteet);
            root.getLapset().add(tkvRepository.save(koulutuksenosatViite));
        }

        tkvRepository.save(root);

        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    private void lisaaPaikallisetTarkennukset(List<SisaltoViite> koulutuksenosat) {
        koulutuksenosat.forEach(koulutuksenosa -> {
            KoulutuksenosanPaikallinenTarkennus koulutuksenosanPaikallinenTarkennus = new KoulutuksenosanPaikallinenTarkennus();
            koulutuksenosa.getKoulutuksenosa().setPaikallinenTarkennus(koulutuksenosanPaikallinenTarkennus);
        });
    }

    private SisaltoViite createKoulutuksenosatViite() {
        SisaltoViite koulutuksenosat = new SisaltoViite();
        TekstiKappale tk = new TekstiKappale();
        Map<Kieli, String> tekstit = new HashMap<>();
        for (Kieli kieli : Kieli.values()) {
            tekstit.put(kieli, messages.translate("koulutuksenosat", kieli));
        }
        tk.setNimi(LokalisoituTeksti.of(tekstit));
        tk.setValmis(true);
        koulutuksenosat.setTekstiKappale(tkRepository.save(tk));
        koulutuksenosat.setPakollinen(true);
        koulutuksenosat.setTyyppi(SisaltoTyyppi.KOULUTUKSENOSAT);
        koulutuksenosat.setLiikkumaton(false);

        return koulutuksenosat;
    }

    private SisaltoViite kopioiHierarkia(SisaltoViite original, Opetussuunnitelma owner, SisaltoViite.TekstiHierarkiaKopiointiToiminto tekstiHierarkiaKopiointiToiminto) {
        SisaltoViite result = original.copy(false, tekstiHierarkiaKopiointiToiminto);
        result.setOwner(owner);
        List<SisaltoViite> lapset = original.getLapset();

        if (lapset != null) {
            for (SisaltoViite lapsi : lapset) {
                SisaltoViite uusiLapsi = kopioiHierarkia(lapsi, owner, tekstiHierarkiaKopiointiToiminto);
                if (uusiLapsi != null) {
                    uusiLapsi.setVanhempi(result);
                    result.getLapset().add(uusiLapsi);
                }
            }
        }

        return tkvRepository.save(result);
    }

    private List<SisaltoViite> koulutuksenosat(SisaltoViite sisaltoviite) {
        List<SisaltoViite> sisaltoviitteet = new ArrayList<>();
        sisaltoviitteet.addAll(sisaltoviite.getLapset().stream().filter(lapsi -> lapsi.getTyyppi().equals(SisaltoTyyppi.KOULUTUKSENOSA)).collect(Collectors.toList()));
        sisaltoviitteet.addAll(sisaltoviite.getLapset().stream()
                .map(lapsi -> koulutuksenosat(lapsi))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return sisaltoviitteet;
    }
}
