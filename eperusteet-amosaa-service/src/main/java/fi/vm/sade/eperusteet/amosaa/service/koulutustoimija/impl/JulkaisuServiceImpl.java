package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuData;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaistuOpetussuunnitelmaTilaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuDataRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InitJacksonConverter;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@Profile("default")
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

    @Autowired
    private JulkaistuOpetussuunnitelmaTilaRepository julkaistuOpetussuunnitelmaTilaRepository;

    @Autowired
    @Lazy
    private JulkaisuService self;

    private static final int JULKAISUN_ODOTUSAIKA_SEKUNNEISSA = 5 * 60;

    private ObjectMapper objectMapper = InitJacksonConverter.createMapper();

    @Override
    @Transactional(readOnly = true)
    public List<JulkaisuBaseDto> getJulkaisut(long ktIds, long opsId) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        if (opetussuunnitelma == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }

        List<JulkaisuBaseDto> julkaisut = mapper.mapAsList(julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelma), JulkaisuBaseDto.class);

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);
        if (julkaistuOpetussuunnitelmaTila != null
                && (julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.KESKEN) || julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.VIRHE))) {
            julkaisut.add(JulkaisuBaseDto.builder()
                    .tila(julkaistuOpetussuunnitelmaTila.getJulkaisutila())
                    .luotu(julkaistuOpetussuunnitelmaTila.getMuokattu())
                    .revision(julkaisut.stream().mapToInt(JulkaisuBaseDto::getRevision).max().orElse(0) + 1)
                    .build());
        }

        return taytaKayttajaTiedot(julkaisut);
    }

    @Override
    public void teeJulkaisu(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);

        if (opetussuunnitelma == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }

        List<Julkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelma);

        if (vanhatJulkaisut.size() > 0 && !onkoMuutoksia(ktId, opsId)) {
            throw new BusinessRuleViolationException("julkaisu-epaonnistui-opetussuunnitelma-ei-muuttunut-viime-julkaisun-jalkeen");
        }

        if (!isValidTiedote(julkaisuBaseDto.getTiedote())) {
            throw new BusinessRuleViolationException("tiedote-sisaltaa-kiellettyja-merkkeja");
        }

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = getOrCreateTila(opsId);
        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.KESKEN);
        saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);

        self.teeJulkaisuAsync(ktId, opsId, julkaisuBaseDto);
    }

    private JulkaistuOpetussuunnitelmaTila getOrCreateTila(Long opsId) {
        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);
        if (julkaistuOpetussuunnitelmaTila == null) {
            julkaistuOpetussuunnitelmaTila = new JulkaistuOpetussuunnitelmaTila();
            julkaistuOpetussuunnitelmaTila.setOpsId(opsId);
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.JULKAISEMATON);
        }

        return julkaistuOpetussuunnitelmaTila;
    }

    @Override
    @Async("julkaisuTaskExecutor")
    public void teeJulkaisuAsync(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        log.debug("teeJulkaisu: {}", opsId);

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = getOrCreateTila(opsId);
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);

        try {

            if (opetussuunnitelma == null) {
                throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
            }

            List<Validointi> validoinnit = opetussuunnitelmaService.validoi(ktId, opsId);

            if (!salliVirheelliset && validoinnit.stream().anyMatch(validointi -> CollectionUtils.isNotEmpty(validointi.getVirheet()))) {
                throw new BusinessRuleViolationException("opetussuunnitelma-ei-validi");
            }

            Julkaisu julkaisu = new Julkaisu();
            kooditaSisaltoviitteet(ktId, opsId);

            OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikki = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ktId, opsId);
            ObjectNode dataJson = (ObjectNode) jsonMapper.toJson(opetussuunnitelmaKaikki);
            List<Julkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelma);

            Set<Long> dokumenttiIds = new HashSet<>();

            for (Kieli kieli : opetussuunnitelma.getJulkaisukielet()) {
                DokumenttiDto dokumenttiDto = dokumenttiService.createDtoFor(ktId, opsId, kieli);
                try {
                    dokumenttiService.setStarted(ktId, opsId, dokumenttiDto);
                    dokumenttiService.generateWithDto(ktId, opsId, dokumenttiDto);
                    dokumenttiIds.add(dokumenttiDto.getId());
                } catch (DokumenttiException e) {
                    log.error(Throwables.getStackTraceAsString(e));
                }
            }

            JulkaisuData julkaisuData = julkaisuDataRepository.save(new JulkaisuData(dataJson));
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            julkaisu.setRevision(vanhatJulkaisut.stream().mapToInt(Julkaisu::getRevision).max().orElse(0) + 1);
            julkaisu.setTiedote(mapper.map(julkaisuBaseDto.getTiedote(), LokalisoituTeksti.class));
            julkaisu.setDokumentit(dokumenttiIds);
            julkaisu.setLuoja(username);
            julkaisu.setLuotu(opetussuunnitelma.getMuokattu());
            julkaisu.setOpetussuunnitelma(opetussuunnitelma);
            julkaisu.setData(julkaisuData);
            julkaisuRepository.saveAndFlush(julkaisu);
            muokkausTietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.VIRHE);
            self.saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
            throw new BusinessRuleViolationException("julkaisun-tallennus-epaonnistui");
        }

        opetussuunnitelma.setTila(Tila.JULKAISTU);
        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.JULKAISTU);
        saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {
        julkaistuOpetussuunnitelmaTilaRepository.save(julkaistuOpetussuunnitelmaTila);
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
                .haeKayttajatiedot(julkaisut.stream().map(JulkaisuBaseDto::getLuoja).filter(Objects::nonNull).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(KayttajanTietoDto::getOidHenkilo, kayttajanTieto -> kayttajanTieto));
        julkaisut.forEach(julkaisu -> julkaisu.setKayttajanTieto(kayttajatiedot.get(julkaisu.getLuoja())));
        return julkaisut;
    }

    private JulkaisuBaseDto taytaKayttajaTiedot(JulkaisuBaseDto julkaisu) {
        return taytaKayttajaTiedot(Arrays.asList(julkaisu)).get(0);
    }

    @Override
    public void kooditaSisaltoviitteet(long ktId, long opsId) {
        SisaltoViiteRakenneDto sisaltoViiteRakenneDto = sisaltoViiteService.getRakenne(ktId, opsId);
        kooditaSisaltoviite(sisaltoviiteRepository.findOneByOwnerIdAndId(opsId, sisaltoViiteRakenneDto.getId()));
    }

    @Override
    public boolean onkoMuutoksia(long ktId, long opsId) {
        try {
            Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
            Julkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

            if (viimeisinJulkaisu == null) {
                return false;
            }

            ObjectNode data = viimeisinJulkaisu.getData().getData();
            String julkaistu = generoiOpetussuunnitelmaKaikkiDtotoString(objectMapper.treeToValue(data, OpetussuunnitelmaKaikkiDto.class));
            String nykyinen = generoiOpetussuunnitelmaKaikkiDtotoString(opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ktId, opsId));

            return JSONCompare.compareJSON(julkaistu, nykyinen, JSONCompareMode.LENIENT).failed();
        } catch (IOException | JSONException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new BusinessRuleViolationException("onko-muutoksia-julkaisuun-verrattuna-tarkistus-epaonnistui");
        }
    }

    @Override
    public JulkaisuTila viimeisinJulkaisuTila(long ktId, long opsId) {
        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);

        if (julkaistuOpetussuunnitelmaTila != null &&
                julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.KESKEN)
                && (new Date().getTime() - julkaistuOpetussuunnitelmaTila.getMuokattu().getTime()) / 1000 > JULKAISUN_ODOTUSAIKA_SEKUNNEISSA) {
            log.error("Julkaisu kesti yli {} sekuntia, opsilla {}", JULKAISUN_ODOTUSAIKA_SEKUNNEISSA, opsId);
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.VIRHE);
            saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
        }

        return julkaistuOpetussuunnitelmaTila != null ? julkaistuOpetussuunnitelmaTila.getJulkaisutila() : JulkaisuTila.JULKAISEMATON;
    }

    private String generoiOpetussuunnitelmaKaikkiDtotoString(OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikki) throws IOException {
        opetussuunnitelmaKaikki.setViimeisinJulkaisuAika(null);
        opetussuunnitelmaKaikki.setTila(null);
        opetussuunnitelmaKaikki.setTila2016(null);
        opetussuunnitelmaKaikki.setMuokattu(null);
        return objectMapper.writeValueAsString(opetussuunnitelmaKaikki);
    }

    private void kooditaSisaltoviite(SisaltoViite sisaltoViite) {
        sisaltoviiteServiceProvider.koodita(sisaltoViite);
        sisaltoViite.getLapset().stream()
                .filter(lapsi -> lapsi != null)
                .forEach(lapsi -> kooditaSisaltoviite(lapsi));
    }

    private boolean isValidTiedote(LokalisoituTekstiDto tiedote) {
        Set<Kieli> kielet = new HashSet<>(Arrays.asList(Kieli.FI, Kieli.SV, Kieli.EN));
        for (Kieli kieli : kielet) {
            if (tiedote.get(kieli) != null && !Jsoup.isValid(tiedote.get(kieli), ValidHtml.WhitelistType.SIMPLIFIED.getWhitelist())) {
                return false;
            }
        }
        return true;
    }
}
