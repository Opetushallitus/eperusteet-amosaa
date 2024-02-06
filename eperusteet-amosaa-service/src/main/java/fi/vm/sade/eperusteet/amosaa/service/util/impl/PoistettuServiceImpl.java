package fi.vm.sade.eperusteet.amosaa.service.util.impl;

import fi.vm.sade.eperusteet.amosaa.domain.Poistettu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.PoistettuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PoistettuServiceImpl implements PoistettuService {
    @Autowired
    DtoMapper mapper;

    @Autowired
    PoistettuRepository repository;

    @Autowired
    OpetussuunnitelmaRepository opsRepository;

    @Autowired
    SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    @Transactional
    public PoistettuDto lisaaPoistettu(Long koulutustoimija, Opetussuunnitelma ops, SisaltoViite osa) {
        if (osa == null || osa.getTekstiKappale() == null || osa.getTekstiKappale().getNimi() == null) {
            return null;
        }
        Poistettu poistettu = new Poistettu();
        poistettu.setMuokkaajaOid(SecurityUtil.getAuthenticatedPrincipal().getName());
        poistettu.setOpetussuunnitelma(ops);
        poistettu.setPoistettu(osa.getId());
        poistettu.setPvm(Calendar.getInstance().getTime());
        poistettu.setNimi(osa.getTekstiKappale().getNimi());
        poistettu.setTyyppi(osa.getTyyppi());
        poistettu.setRev(sisaltoviiteRepository.getLatestRevisionId(osa.getId()));
        return mapper.map(repository.save(poistettu), PoistettuDto.class);
    }

    @Override
    public List<PoistettuDto> poistetut(Long koulutustoimijaId, Long opsId) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        List<Poistettu> poistetut = repository.findAllByOpetussuunnitelma(ops);
        return mapper.mapAsList(poistetut, PoistettuDto.class);
    }
}
