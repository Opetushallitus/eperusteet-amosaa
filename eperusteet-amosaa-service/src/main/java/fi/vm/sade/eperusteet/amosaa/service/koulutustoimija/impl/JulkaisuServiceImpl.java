package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
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
import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaMuokkaustietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaistuOpetussuunnitelmaTilaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuDataRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import jakarta.annotation.PostConstruct;

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

    @Lazy
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

    @Autowired
    private CacheManager cacheManager;

    private static final int JULKAISUN_ODOTUSAIKA_SEKUNNEISSA = 60 * 60;

    private static final String PATH_PARAMS_KEY_REGEX = "[a-zA-Z0-9_-]+";
    private static final String FILTER_KEY_PARAMS_REGEX = "[a-zA-Z0-9_.-]+";
    private static final String FILTER_VALUE_PARAMS_REGEX = "[a-zA-Z0-9_ .-]+";

    private ObjectMapper objectMapper = InitJacksonConverter.createMapper();

    private ObjectMapper objMapper;

    @PostConstruct
    protected void initObjMapper() {
        objMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objMapper.registerModule(module);
        objMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

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
                    dokumenttiService.generateWithDto(ktId, opsId, dokumenttiDto, opetussuunnitelmaKaikki);
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
        opetussuunnitelma.setEsikatseltavissa(false);
        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.JULKAISTU);
        self.saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
        cacheManager.getCache("ops-navigation").evictIfPresent(opsId);
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
    public boolean julkaisemattomiaMuutoksia(long ktId, long opsId) {
        List<OpetussuunnitelmaMuokkaustietoDto> muokkaustiedot = muokkausTietoService.getOpetussuunnitelmanMuokkaustiedot(ktId, opsId, new Date(), 1);
        return julkaisuRepository.countByOpetussuunnitelmaId(opsId) > 0
                && !muokkaustiedot.isEmpty()
                && muokkaustiedot.stream().noneMatch(muokkaustieto -> muokkaustieto.getTapahtuma().equals(MuokkausTapahtuma.JULKAISU));
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
                .filter(Objects::nonNull)
                .forEach(this::kooditaSisaltoviite);
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

    @Override
    @Transactional(readOnly = true)
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmatJulkaisut(OpetussuunnitelmaJulkaistuQueryDto pquery) {
        Pageable pageable = PageRequest.of(pquery.getSivu(), pquery.getSivukoko());
        List<String> koulutustyypit = pquery.getKoulutustyyppi().stream().map(KoulutusTyyppi::toString).collect(Collectors.toList());
        koulutustyypit.addAll(pquery.getKoulutustyyppi().stream().map(KoulutusTyyppi::name).collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(koulutustyypit) || (CollectionUtils.isNotEmpty(pquery.getTyyppi()) && pquery.getTyyppi().contains(OpsTyyppi.YHTEINEN.toString()))) {
            koulutustyypit = List.of();
        }

        return julkaisuRepository.findAllJulkisetJulkaisut(
                koulutustyypit,
                pquery.getNimi(),
                pquery.getKieli(),
                pquery.getOppilaitosTyyppiKoodiUri(),
                pquery.getTyyppi(),
                pquery.getOrganisaatio(),
                pquery.getPerusteId(),
                pquery.getPerusteenDiaarinumero(),
                pquery.isTuleva(),
                pquery.isVoimassaolo(),
                pquery.isPoistunut(),
                pquery.getJotpatyyppi(),
                pquery.getJotpatyyppi().contains("NULL"),
                DateTime.now().getMillis(),
                pageable)
                .map(this::convertToOpetussuunnitelmaDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        Julkaisu julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);

        if (julkaisu == null) {
            return null;
        }

        ObjectNode data = julkaisu.getData().getData();
        try {
            ObjectMapper om = InitJacksonConverter.createMapper();
            OpetussuunnitelmaKaikkiDto kaikkiDto = om.treeToValue(data, OpetussuunnitelmaKaikkiDto.class);
            kaikkiDto.setTila(Tila.JULKAISTU);
            return kaikkiDto;
        } catch (JsonProcessingException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new BusinessRuleViolationException("opetussuunnitelman-haku-epaonnistui");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SisaltoviiteOpintokokonaisuusExternalDto findJulkaistuOpintokokonaisuus(String koodiArvo) throws IOException {
        String opetussuunnitelma = julkaisuRepository.findByOpintokokonaisuusKoodiArvo(koodiArvo);
        if (opetussuunnitelma != null) {
            OpetussuunnitelmaKaikkiDto opsKaikki = objMapper.readValue(opetussuunnitelma, OpetussuunnitelmaKaikkiDto.class);
            SisaltoViiteExportOpintokokonaisuusDto opintokokonaisuusViite = opsKaikki.getOpintokokonaisuudet().stream()
                    .filter(opintokokonaisuus -> opintokokonaisuus.getOpintokokonaisuus().getKoodiArvo() != null)
                    .filter(opintokokonaisuus -> opintokokonaisuus.getOpintokokonaisuus().getKoodiArvo().equals(koodiArvo))
                    .findFirst().orElseThrow(() -> new IOException("Virhe luettaessa opintokokonaisuutta opetussuunnitelmasta"));

            SisaltoviiteOpintokokonaisuusExternalDto opintokokonaisuusExternalDto = mapper.map(opintokokonaisuusViite, SisaltoviiteOpintokokonaisuusExternalDto.class);
            opintokokonaisuusExternalDto.setOpetussuunnitelmaId(opsKaikki.getId());

            return opintokokonaisuusExternalDto;
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getJulkaistuSisaltoObjectNode(Long opetussuunnitelmaId, List<String> paths, Map<String, String> filters) {
        paths.forEach(path -> {
            if (!path.matches(PATH_PARAMS_KEY_REGEX)) {
                throw new NotExistsException("");
            }
        });

        if (!ObjectUtils.isEmpty(filters)) {
            filters.forEach((key, value) -> {
                if (!key.matches(FILTER_KEY_PARAMS_REGEX) || !value.matches(FILTER_VALUE_PARAMS_REGEX)) {
                    throw new NotExistsException("");
                }
            });
        }

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opetussuunnitelmaId);

        if (opetussuunnitelma == null || opetussuunnitelma.getTila().equals(Tila.POISTETTU)) {
            throw new NotExistsException("");
        }

        String query = paths.stream().reduce("$", (path, element) -> {
            if (NumberUtils.isCreatable(element)) {
                return path + String.format("?(@.id==%s)", element);
            }
            return path + "." + element.toLowerCase();
        });

        try {
            Object result = objMapper.readValue(julkaisuRepository.findJulkaisutByJsonPath(opetussuunnitelmaId, query), Object.class);
            if (result instanceof List && !ObjectUtils.isEmpty(filters)) {
                result = filterJsonList((List<?>) result, filters);
            }
            return result;
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new NotExistsException("");
        }
    }

    private OpetussuunnitelmaDto convertToOpetussuunnitelmaDto(String obj) {
        try {
            return objMapper.readValue(obj, OpetussuunnitelmaDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert to OpetussuunnitelmaDto", e);
            throw new RuntimeException(e);
        }
    }

    private List<?> filterJsonList(List<?> list, Map<String, String> filters) {
        return list.stream()
                .filter(item -> item instanceof Map && matchesFilters((Map<?, ?>) item, filters))
                .collect(Collectors.toList());
    }

    private boolean matchesFilters(Map<?, ?> item, Map<String, String> filters) {
        return filters.entrySet().stream().allMatch(entry -> {
            Object value = getNestedValue(item, entry.getKey());
            if (value == null) {
                return false;
            }
            String filterValue = entry.getValue();
            String itemValueStr = value.toString();
            return filterValue.equals(itemValueStr);
        });
    }

    private Object getNestedValue(Map<?, ?> item, String path) {
        String[] parts = path.split("\\.");
        Object current = item;
        for (String part : parts) {
            if (current == null || !(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(part);
        }
        return current;
    }
}
