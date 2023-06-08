package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class KoulutustoimijaYhteinenOsuusCreateService implements OpetussuunnitelmaCreateService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Override
    public Set<OpsTyyppi> getOpsTyypit() {
        return Sets.newHashSet(OpsTyyppi.YHTEINEN);
    }

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

    @Override
    public OpetussuunnitelmaBaseDto create(Koulutustoimija koulutustoimija, OpetussuunnitelmaLuontiDto opsDto) {
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(opsDto.getOpsId());
        Opetussuunnitelma opetussuunnitelma = mapper.map(opsDto, Opetussuunnitelma.class);
        opetussuunnitelma.setPohja(getPohja(pohja));
        opetussuunnitelma.changeKoulutustoimija(koulutustoimija);
        opetussuunnitelma.setTila(Tila.LUONNOS);
        opetussuunnitelma.setTyyppi(OpsTyyppi.YHTEINEN);
        opetussuunnitelma.setSuoritustapa("yhteinen");
        opetussuunnitelma = opetussuunnitelmaRepository.save(opetussuunnitelma);

        SisaltoViite pohjaRoot = sisaltoviiteRepository.findOneRoot(pohja);
        SisaltoViite root = sisaltoViiteService.kopioiHierarkia(
                pohjaRoot,
                opetussuunnitelma,
                null,
                kopiointiToiminto(pohja));
        sisaltoviiteRepository.save(root);

        return mapper.map(opetussuunnitelma, OpetussuunnitelmaBaseDto.class);
    }

    private Opetussuunnitelma getPohja(Opetussuunnitelma pohja) {
        List<Opetussuunnitelma> pohjat = getPohjat(pohja);

        if (pohjat.isEmpty() || pohjat.size() == 1) { // pohjana joko oph-pohja tai koulutustoimijan opetussuunnitelma, jonka pohjana oph-pohja
            return pohja;
        }

        return pohjat.get(1); // pohjaksi asetetaan hierarkiassa korkemmallaan opetussuunnitelma, jonka pohjana on oph-pohja
    }

    private SisaltoViite.TekstiHierarkiaKopiointiToiminto kopiointiToiminto(Opetussuunnitelma pohja) {
        List<Opetussuunnitelma> pohjat = getPohjat(pohja);

        if (pohjat.isEmpty()) { // käytössä OPH-pohja
            return SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI;
        }

        if (pohjat.size() == 1) { // käytössä koulutustoimijan tekemä opetussuunnitelma, jonka pohjana OPH-pohja
            return SisaltoViite.TekstiHierarkiaKopiointiToiminto.POHJAVIITE;
        }

        // käytössä koulutustoimijan tekemä opetussuunnitelma, jonka pohjana toinen koulutustoimijan tekemä opetussuunnitelma
        return SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI_JA_SAILYTA_POHJA_VIITE;
    }

    private List<Opetussuunnitelma> getPohjat(Opetussuunnitelma opetussuunnitelma) {
        List<Opetussuunnitelma> pohjat = new ArrayList<>();
        Opetussuunnitelma pohja = opetussuunnitelma.getPohja();
        while (pohja != null) {
            pohjat.add(pohja);
            pohja = pohja.getPohja();
        }

        return Lists.reverse(pohjat); // reverse jotta ensimäisenä aina oph-pohja
    }

}
