package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KotoOpetussuunnitelmaCreateService implements OpetussuunnitelmaCreateService {

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private SisaltoviiteRepository tkvRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private SisaltoViiteService tkvService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS);
    }

    @Override
    public OpetussuunnitelmaBaseDto create(Koulutustoimija koulutustoimija, OpetussuunnitelmaLuontiDto opsDto) {
        Opetussuunnitelma ops;
        Opetussuunnitelma pohja = repository.findOne(opsDto.getOpsId());
        SisaltoViite sisaltoRoot = tkvRepository.findOneRoot(pohja);
        ops = pohja.copy();
        ops.setPohja(pohja);
        ops.setNimi(mapper.map(opsDto.getNimi(), LokalisoituTeksti.class));
        ops = repository.save(ops);
        SisaltoViite root = tkvService.kopioiHierarkia(sisaltoRoot, ops, Collections.emptyMap(), !pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA));
        tkvRepository.save(root);
        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }
}
