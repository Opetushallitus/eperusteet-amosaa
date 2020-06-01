package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpetussuunnitelmaAikataulu;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaAikatauluDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaAikatauluService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OpetussuunnitelmaAikatauluServiceImpl implements OpetussuunnitelmaAikatauluService {

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<OpetussuunnitelmaAikatauluDto> save(Long ktId, Long opsId, List<OpetussuunnitelmaAikatauluDto> opetussuunnitelmaAikataulut) {

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.getOne(opsId);
        List<OpetussuunnitelmaAikataulu> aikataulut = mapper.mapAsList(opetussuunnitelmaAikataulut, OpetussuunnitelmaAikataulu.class).stream().map(aikataulu -> {
            aikataulu.setOpetussuunnitelma(opetussuunnitelma);
            return aikataulu;
        }).collect(Collectors.toList());
        opetussuunnitelma.setOpetussuunnitelmanAikataulut(aikataulut);

        opetussuunnitelmaRepository.save(opetussuunnitelma);

        return mapper.mapAsList(opetussuunnitelma.getOpetussuunnitelmanAikataulut(), OpetussuunnitelmaAikatauluDto.class);
    }

    @Override
    public List<OpetussuunnitelmaAikatauluDto> get(Long ktId, Long opsId) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.getOne(opsId);
        return mapper.mapAsList(opetussuunnitelma.getOpetussuunnitelmanAikataulut(), OpetussuunnitelmaAikatauluDto.class);
    }
}
