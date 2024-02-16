package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.Termi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.TermistoRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TermistoServiceImpl implements TermistoService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    TermistoRepository termistoRepository;

    @Autowired
    KoulutustoimijaRepository koulutustoimijaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TermiDto> getTermit(Long ktId) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        List<Termi> termit = termistoRepository.findAllByKoulutustoimija(toimija);
        return mapper.mapAsList(termit, TermiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TermiDto getTermi(Long ktId, Long id) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        Termi termi = termistoRepository.findOneByKoulutustoimijaAndId(toimija, id);
        return mapper.map(termi, TermiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TermiDto getTermiByAvain(Long ktId, String avain) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        Termi termi = termistoRepository.findOneByKoulutustoimijaAndAvain(toimija, avain);
        return mapper.map(termi, TermiDto.class);
    }

    @Override
    public TermiDto addTermi(Long ktId, TermiDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Termi tmp = mapper.map(dto, Termi.class);
        tmp.setAvain(generateKey());
        tmp.setKoulutustoimija(toimija);
        tmp = termistoRepository.save(tmp);
        return mapper.map(tmp, TermiDto.class);
    }

    @Override
    public TermiDto updateTermi(Long ktId, TermiDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Termi current = termistoRepository.findOne(dto.getId());
        assertExists(current, "Päivitettävää tietoa ei ole olemassa");
        dto.setAvain(current.getAvain());
        mapper.map(dto, current);
        termistoRepository.save(current);
        return mapper.map(current, TermiDto.class);
    }

    @Override
    public void deleteTermi(Long ktId, Long id) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Termi termi = termistoRepository.findOneByKoulutustoimijaAndId(toimija, id);
        termistoRepository.delete(termi);
    }

    private static String generateKey() {
        return "avain_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    private static void assertExists(Object o, String msg) {
        if (o == null) {
            throw new NotExistsException(msg);
        }
    }
}
