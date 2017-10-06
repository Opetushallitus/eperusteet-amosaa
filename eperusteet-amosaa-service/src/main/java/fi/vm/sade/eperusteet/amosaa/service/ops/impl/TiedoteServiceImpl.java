package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaKuittaus;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaKuittausRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.TiedoteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by richard.vancamp on 29/03/16.
 */

@Service
@Transactional
public class TiedoteServiceImpl implements TiedoteService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    TiedoteRepository tiedoteRepository;

    @Autowired
    KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    @Autowired
    KayttajaKuittausRepository kuittausRepository;

    @Override
    public List<TiedoteDto> getJulkisetTiedotteet(Long ktId) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        if (toimija == null) {
            return new ArrayList<>();
        }
        List<Tiedote> tiedotteet = tiedoteRepository.findAllByKoulutustoimijaAndJulkinenTrue(toimija);
        return mapper.mapAsList(tiedotteet, TiedoteDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TiedoteDto> getTiedotteet(Long ktId) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        Koulutustoimija oph = koulutustoimijaRepository.findOph();
        List<Tiedote> tiedotteet = tiedoteRepository.findAllByKoulutustoimija(toimija);
        tiedotteet.addAll(tiedoteRepository.findAllByKoulutustoimijaAndJulkinenTrue(oph));
        Set<Long> kuitatut = kuittausRepository.findAllByKayttaja(kayttajanTietoService.getKayttaja().getId());
        Set<Tiedote> uniikit = tiedotteet.stream()
                .filter(tiedote -> !kuitatut.contains(tiedote.getId()))
                .collect(Collectors.toSet());
        return mapper.mapAsList(uniikit, TiedoteDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TiedoteDto getTiedote(Long ktId, Long id) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        Tiedote tiedote = tiedoteRepository.findOneByKoulutustoimijaAndId(toimija, id);
        return mapper.map(tiedote, TiedoteDto.class);
    }

    @Override
    public TiedoteDto addTiedote(Long ktId, TiedoteDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Tiedote tmp = mapper.map(dto, Tiedote.class);
        tmp.setKoulutustoimija(toimija);
        tmp.setLuotu(Calendar.getInstance().getTime());
        tmp.setLuoja(kayttajanTietoService.getUserOid());
        tmp = tiedoteRepository.save(tmp);
        return mapper.map(tmp, TiedoteDto.class);
    }

    @Override
    public TiedoteDto updateTiedote(Long ktId, TiedoteDto dto) {
        Koulutustoimija toimija = koulutustoimijaRepository.findOne(ktId);
        assertExists(toimija, "Koulutustoimija ei ole olemassa");
        Tiedote current = tiedoteRepository.findOne(dto.getId());
        assertExists(current, "Päivitettävää tietoa ei ole olemassa");
        Tiedote tmp = mapper.map(dto, current);
        tmp.setMuokattu(Calendar.getInstance().getTime());
        tmp.setMuokkaaja(kayttajanTietoService.getUserOid());
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

    @Override
    public void kuittaaLuetuksi(Long ktId, Long id) {
        Kayttaja kayttaja = kayttajanTietoService.getKayttaja();
        kuittausRepository.save(new KayttajaKuittaus(kayttaja.getId(), id));
    }

}
