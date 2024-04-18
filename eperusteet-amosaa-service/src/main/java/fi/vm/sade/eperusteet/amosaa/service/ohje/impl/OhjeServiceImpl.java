package fi.vm.sade.eperusteet.amosaa.service.ohje.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.amosaa.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.amosaa.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ohje.OhjeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OhjeServiceImpl implements OhjeService {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OhjeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<OhjeDto> getOhjeet(KoulutustyyppiToteutus toteutus) {
        return mapper.mapAsList(repository.findByToteutus(toteutus), OhjeDto.class);
    }

    @Override
    public OhjeDto addOhje(OhjeDto dto) {
        Ohje ohje = mapper.map(dto, Ohje.class);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public OhjeDto editOhje(Long id, OhjeDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BusinessRuleViolationException("id-ei-tasmaa");
        }
        Ohje ohje = repository.findOne(id);
        ohje = mapper.map(dto, ohje);
        return mapper.map(ohje, dto);
    }

    @Override
    public void removeOhje(Long id) {
        repository.deleteById(id);
    }

}
