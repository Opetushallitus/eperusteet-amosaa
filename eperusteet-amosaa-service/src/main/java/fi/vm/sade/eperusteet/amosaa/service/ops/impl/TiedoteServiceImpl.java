package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.Termi;
import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.TiedoteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

/**
 * Created by richard.vancamp on 29/03/16.
 */
public class TiedoteServiceImpl {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    TiedoteRepository tiedoteRepository;

    @Autowired
    KoulutustoimijaRepository koulutustoimijaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TiedoteDto> getAll(Long ktId) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);;
        List<Tiedote> tiedotteet = tiedoteRepository.findAll(toimija);
        return mapper.mapAsList(tiedotteet, TiedoteDto.class);
    }

    @Override
    public TiedoteDto addTiedote(Long ktId, TiedoteDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Tiedote tmp = mapper.map(dto, Tiedote.class);
        tmp.setKoulutustoimija(toimija);
        tmp = tiedoteRepository.save(tmp);
        return mapper.map(tmp, TiedoteDto.class);
    }

    @Override
    public TiedoteDto updateTiedote(Long ktId, TiedoteDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Tiedote current = tiedoteRepository.findOne(dto.getId());
        assertExists(current, "Päivitettävää tietoa ei ole olemassa");
        mapper.map(dto, current);
        tiedoteRepository.save(current);
        return mapper.map(current, TiedoteDto.class);
    }

    @Override
    public void deleteTiedote(Long ktId, Long id) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Tiedote tiedote = tiedoteRepository.findOneByKoulutustoimijaAndId(toimija, id);
        tiedoteRepository.delete(tiedote);
    }


}
