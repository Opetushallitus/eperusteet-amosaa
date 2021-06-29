package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuData;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuDataRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@Transactional
public class JulkaisuServiceImpl implements JulkaisuService {

    @Value("${fi.vm.sade.eperusteet.salli_virheelliset:false}")
    private boolean salliVirheelliset;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService muokkausTietoService;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private JulkaisuDataRepository julkaisuDataRepository;

    @Autowired
    private SisaltoviiteServiceProvider sisaltoviiteServiceProvider;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JulkaisuBaseDto> getJulkaisut(long ktIds, long opsId) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        if (opetussuunnitelma == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }

        List<JulkaisuBaseDto> julkaisut = mapper.mapAsList(julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelma), JulkaisuBaseDto.class);

        try {
            julkaisut = taytaKayttajaTiedot(julkaisut);
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
        }

        return julkaisut;
    }

    @Override
    public JulkaisuBaseDto teeJulkaisu(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);

        if (opetussuunnitelma == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }

        Validointi validointi = opetussuunnitelmaService.validoi(ktId, opsId);

        if (!salliVirheelliset && CollectionUtils.isNotEmpty(validointi.getVirheet())) {
            throw new BusinessRuleViolationException("opetussuunnitelma-ei-validi");
        }

        Julkaisu julkaisu = new Julkaisu();
        try {
            Julkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);
            OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikki = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ktId, opsId);
            ObjectNode dataJson = (ObjectNode) jsonMapper.toJson(opetussuunnitelmaKaikki);

            if (viimeisinJulkaisu != null) {
                int lastHash = viimeisinJulkaisu.getData().getHash();
                int currentHash = dataJson.hashCode();
                if (lastHash == currentHash) {
                    throw new BusinessRuleViolationException("ei-muuttunut-viime-julkaisun-jalkeen");
                }
            }

            kooditaSisaltoviitteet(ktId, opsId);

            for (Kieli kieli : opetussuunnitelma.getJulkaisukielet()) {
                try {
                    DokumenttiDto dokumenttiDto = dokumenttiService.getDto(ktId, opsId, kieli);
                    dokumenttiService.setStarted(ktId, opsId, dokumenttiDto);
                    dokumenttiService.generateWithDto(ktId, opsId, dokumenttiDto);
                } catch (DokumenttiException e) {
                    log.error(e.getLocalizedMessage(), e.getCause());
                }
            }

            Set<Long> dokumentit = opetussuunnitelma.getJulkaisukielet().stream()
                    .map(kieli -> dokumenttiService.getDto(ktId, opsId, kieli))
                    .map(DokumenttiDto::getId)
                    .collect(toSet());

            JulkaisuData julkaisuData = julkaisuDataRepository.save(new JulkaisuData(dataJson));
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
            julkaisu.setTiedote(mapper.map(julkaisuBaseDto.getTiedote(), LokalisoituTeksti.class));
            julkaisu.setDokumentit(dokumentit);
            julkaisu.setLuoja(username);
            julkaisu.setLuotu(opetussuunnitelma.getMuokattu());
            julkaisu.setOpetussuunnitelma(opetussuunnitelma);
            julkaisu.setData(julkaisuData);
            julkaisu = julkaisuRepository.save(julkaisu);
            muokkausTietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessRuleViolationException("julkaisun-tallennus-epaonnistui");
        }

        return taytaKayttajaTiedot(mapper.map(julkaisu, JulkaisuBaseDto.class));
    }

    @Override
    public JulkaisuBaseDto aktivoiJulkaisu(long ktId, long opsId, int revision) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        Julkaisu vanhaJulkaisu = julkaisuRepository.findByOpetussuunnitelmaAndRevision(opetussuunnitelma, revision);
        Julkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

        Julkaisu julkaisu = new Julkaisu();
        julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
        julkaisu.setTiedote(vanhaJulkaisu.getTiedote());
        julkaisu.setDokumentit(Sets.newHashSet(vanhaJulkaisu.getDokumentit()));
        julkaisu.setOpetussuunnitelma(opetussuunnitelma);
        julkaisu.setData(vanhaJulkaisu.getData());
        julkaisu = julkaisuRepository.save(julkaisu);
        muokkausTietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);

        return taytaKayttajaTiedot(mapper.map(julkaisu, JulkaisuBaseDto.class));
    }

    private List<JulkaisuBaseDto> taytaKayttajaTiedot(List<JulkaisuBaseDto> julkaisut) {
        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(julkaisut.stream().map(JulkaisuBaseDto::getLuoja).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));
        julkaisut.forEach(julkaisu -> julkaisu.setKayttajanTieto(kayttajatiedot.get(julkaisu.getLuoja())));
        return julkaisut;
    }

    private JulkaisuBaseDto taytaKayttajaTiedot(JulkaisuBaseDto julkaisu) {
        return taytaKayttajaTiedot(Arrays.asList(julkaisu)).get(0);
    }

    private void kooditaSisaltoviitteet(long ktId, long opsId) {
        SisaltoViiteRakenneDto sisaltoViiteRakenneDto = sisaltoViiteService.getRakenne(ktId, opsId);
        kooditaSisaltoviite(sisaltoviiteRepository.findOneByOwnerIdAndId(opsId, sisaltoViiteRakenneDto.getId()));
    }

    private void kooditaSisaltoviite(SisaltoViite sisaltoViite) {
        sisaltoviiteServiceProvider.koodita(sisaltoViite);
        sisaltoViite.getLapset().forEach(lapsi -> kooditaSisaltoviite(lapsi));
    }

}
