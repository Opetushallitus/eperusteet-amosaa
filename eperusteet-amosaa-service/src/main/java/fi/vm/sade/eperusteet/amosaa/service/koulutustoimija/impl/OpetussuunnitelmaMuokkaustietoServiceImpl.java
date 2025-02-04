package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpetussuunnitelmaMuokkaustieto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaMuokkaustietoDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaMuokkaustietoRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class OpetussuunnitelmaMuokkaustietoServiceImpl implements OpetussuunnitelmaMuokkaustietoService {

    @Autowired
    private OpetussuunnitelmaMuokkaustietoRepository muokkausTietoRepository;

    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public List<OpetussuunnitelmaMuokkaustietoDto> getOpetussuunnitelmanMuokkaustiedot(Long ktId, Long opsId, Date viimeisinLuontiaika, int lukumaara) {

        List<OpetussuunnitelmaMuokkaustietoDto> muokkaustiedot = mapper
                .mapAsList(muokkausTietoRepository.findTop10ByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(opsId, viimeisinLuontiaika, lukumaara), OpetussuunnitelmaMuokkaustietoDto.class);

        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(muokkaustiedot.stream()
                        .map(OpetussuunnitelmaMuokkaustietoDto::getMuokkaaja)
                        .collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(KayttajanTietoDto::getOidHenkilo, kayttajanTieto -> kayttajanTieto));

        muokkaustiedot.forEach(muokkaustieto -> muokkaustieto.setKayttajanTieto(kayttajatiedot.get(muokkaustieto.getMuokkaaja())));

        return muokkaustiedot;
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, historiaTapahtuma.getNavigationType(), null);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, String lisatieto) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, historiaTapahtuma.getNavigationType(), lisatieto);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, navigationType, null);
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto) {
        addOpsMuokkausTieto(opsId, historiaTapahtuma, muokkausTapahtuma, navigationType, lisatieto, SecurityUtil.getAuthenticatedPrincipal().getName());
    }

    @Override
    public void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto, String muokkaaja) {
        try {
            // Merkataan aiemmat tapahtumat poistetuksi
            if (Objects.equals(muokkausTapahtuma.getTapahtuma(), MuokkausTapahtuma.POISTO.toString())) {
                List<OpetussuunnitelmaMuokkaustieto> aiemminTapahtumat = muokkausTietoRepository
                        .findByKohdeId(historiaTapahtuma.getId()).stream()
                        .peek(tapahtuma -> tapahtuma.setPoistettu(true))
                        .collect(Collectors.toList());
                muokkausTietoRepository.saveAll(aiemminTapahtumat);
            }

            // Lisätään uusi tapahtuma
            OpetussuunnitelmaMuokkaustieto muokkaustieto = OpetussuunnitelmaMuokkaustieto.builder()
                    .opetussuunnitelmaId(opsId)
                    .nimi(historiaTapahtuma.getNimi())
                    .tapahtuma(muokkausTapahtuma)
                    .muokkaaja(muokkaaja)
                    .kohde(navigationType)
                    .kohdeId(historiaTapahtuma.getId())
                    .luotu(new Date())
                    .lisatieto(lisatieto)
                    .poistettu(Objects.equals(muokkausTapahtuma.getTapahtuma(), MuokkausTapahtuma.POISTO.toString()))
                    .build();

            muokkausTietoRepository.save(muokkaustieto);
        } catch (RuntimeException e) {
            log.error("Historiatiedon lisääminen epäonnistui", e);
        }
    }
}

