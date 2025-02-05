package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuInternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaListausDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaWithLatestTilaUpdateTime;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioStatus;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Suoritustapakoodi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.KoulutuskoodiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaDispatcher;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaPohjaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaSisaltoCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.service.ops.impl.OpetussuunnitelmaSisaltoCreateUtil;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import fi.vm.sade.eperusteet.amosaa.service.security.KoulutustyyppiRolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.NavigationUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {
    static private final Logger LOG = LoggerFactory.getLogger(OpetussuunnitelmaServiceImpl.class);

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private TekstiKappaleRepository tkRepository;

    @Autowired
    private SisaltoviiteRepository tkvRepository;

    @Autowired
    private SisaltoViiteService tkvService;

    @Autowired
    private ValidointiService validointiService;

    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Lazy
    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutuskoodiRepository koulutuskoodiRepository;

    @Autowired
    public void setKoulutustoimijaService(KoulutustoimijaService kts) {
        this.koulutustoimijaService = kts;
    }

    public KoulutustoimijaService getKoulutustoimijaService() {
        return this.koulutustoimijaService;
    }

    @Autowired
    private LocalizedMessagesService messages;

    private ObjectMapper objMapper;

    @Autowired
    private OpetussuunnitelmaDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService muokkausTietoService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private PermissionManager permissionManager;

    @Autowired
    private OpetussuunnitelmaSisaltoCreateService opetussuunnitelmaSisaltoCreateService;

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    protected void init() {
        objMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objMapper.registerModule(module);
        objMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    @Transactional
    public void mapPerusteIds() {
        List<CachedPeruste> cperusteet = cachedPerusteRepository.findAll();
        for (CachedPeruste cperuste : cperusteet) {
            if (cperuste.getPerusteId() == null) {
                try {
                    JsonNode peruste = eperusteetService.getPerusteSisalto(cperuste, JsonNode.class);
                    cperuste.setPerusteId(peruste.get("id").asLong());
                } catch (Exception ex) {
                    LOG.error(ex.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void mapKoulutustyyppi() {
        List<CachedPeruste> cperusteet = cachedPerusteRepository.findAll();
        for (CachedPeruste cperuste : cperusteet) {
            if (cperuste.getKoulutustyyppi() == null) {
                try {
                    JsonNode peruste = eperusteetService.getPerusteSisalto(cperuste, JsonNode.class);
                    cperuste.setKoulutustyyppi(KoulutusTyyppi.of(peruste.get("koulutustyyppi").asText()));
                } catch (Exception ex) {
                    LOG.error(ex.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public List<OpetussuunnitelmaDto> getJulkisetOpetussuunnitelmat(Long ktId) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimijaAndTila(koulutustoimija, Tila.JULKAISTU);
        return mapper.mapAsList(opsit, OpetussuunnitelmaDto.class);
    }

    @Override
    public <T> Page<T> getOpetussuunnitelmat(
            Long ktId,
            PageRequest page,
            OpsHakuDto query,
            Class<T> clazz
    ) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);

        List<KoulutusTyyppi> koulutustyyppi = query.getKoulutustyyppi();
        if (koulutustyyppi == null && query.getTyyppi() != null && query.getTyyppi().contains(OpsTyyppi.YHTEINEN)) {
            koulutustyyppi = List.of(KoulutusTyyppi.PERUSTUTKINTO);
        }

        if (koulutustyyppi == null || koulutustyyppi.stream().noneMatch(kt -> SecurityUtil.isUserOphAdmin(KoulutustyyppiRolePrefix.of(kt)))) {
            query.setKoulutustoimijat(Collections.singletonList(koulutustoimija.getId()));
        }

        return repository.findBy(page, query)
                .map(ops -> mapper.map(ops, clazz));
    }

    @Override
    public List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(Set<KoulutusTyyppi> koulutusTyyppi) {
        return repository.findOpetussuunnitelmaTilastot(koulutusTyyppi).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaTilastoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(Integer sivu, Integer sivukoko) {
        Pageable pageable = PageRequest.of(sivu, sivukoko, Sort.Direction.ASC, "id");
        List<OpetussuunnitelmaTilastoDto> opetussuunnitelmatDto = repository.findAllByTyyppiIn(List.of(OpsTyyppi.OPS, OpsTyyppi.YHTEINEN), pageable).getContent().stream()
                .map(t -> mapper.map(t, OpetussuunnitelmaTilastoDto.class))
                .collect(Collectors.toList());

        Map<Long, Date> opetussuunnitelmaWithLatestTilaUpdateTimesMaps = repository.findAllWithLatestTilaUpdateDate(
                        opetussuunnitelmatDto.stream().map(OpetussuunnitelmaTilastoDto::getId).collect(Collectors.toList()), List.of(OpsTyyppi.OPS.name(), OpsTyyppi.YHTEINEN.name()))
                .stream().collect(Collectors.toMap(OpetussuunnitelmaWithLatestTilaUpdateTime::getId, OpetussuunnitelmaWithLatestTilaUpdateTime::getViimeisinTilaMuutosAika));

        return new PageImpl(opetussuunnitelmatDto.stream()
                .peek(opsDto -> opsDto.setViimeisinTilaMuutosAika(opetussuunnitelmaWithLatestTilaUpdateTimesMaps.get(opsDto.getId())))
                .collect(Collectors.toList()), pageable, opetussuunnitelmatDto.size());
    }

    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmat(Long ktId) {
        return repository.findAllByKoulutustoimijaId(ktId).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmat(Long ktId, OpsTyyppi tyyppi) {
        return repository.findAllByKoulutustoimijaIdAndTyyppi(ktId, tyyppi).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<OpetussuunnitelmaListausDto> getOpetussuunnitelmat(Long ktId, OpsHakuInternalDto query) {
        PageRequest pageRequest = PageRequest.of(query.getSivu(), query.getSivukoko(), Sort.by(Sort.Direction.fromString("ASC"), "nimi.teksti"));

        if (query.getKoulutustyyppi().stream().noneMatch(kt -> SecurityUtil.isUserOphAdmin(KoulutustyyppiRolePrefix.of(KoulutusTyyppi.of(kt))))) {
            query.setKoulutustoimijat(Arrays.asList(ktId));
        }

        if (CollectionUtils.isEmpty(query.getKoulutustoimijat())) {
            query.setKoulutustoimijat(null);
        }

        return repository.findByKoulutustoimijaInAndPerusteKoulutustyyppiInAndOpsTyyppi(
                        query.getKoulutustoimijat(),
                        query.getKoulutustyyppi().stream().map(KoulutusTyyppi::of).collect(Collectors.toList()),
                        query.getNimi(),
                        Kieli.of(query.getKieli()),
                        query.getJotpa(),
                        query.getJulkaistuTaiValmis(),
                        query.getTyyppi(),
                        query.isPoistunut(),
                        pageRequest)
                .map(ops -> mapper.map(ops, OpetussuunnitelmaListausDto.class));
    }

    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmat(Long ktId, Set<String> koulutustyypit, Set<Tila> tilat) {
        return repository.findByKoulutustoimijaIdAndPerusteKoulutustyyppiIn(ktId, koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()), tilat).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmat(Long ktId, Set<String> koulutustyypit, OpsTyyppi tyyppi) {
        return repository.findByKoulutustoimijaIdAndPerusteKoulutustyyppiIn(ktId, koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()), tyyppi).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(
            Long ktId,
            PageRequest page,
            OpsHakuDto query
    ) {
        return getOpetussuunnitelmat(ktId, page, query, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public <T extends OpetussuunnitelmaBaseDto> List<T> getJulkaistutPerusteenOpetussuunnitelmat(String diaari, Class<T> type) {
        List<Opetussuunnitelma> opsit = repository.findAllJulkaistutByPerusteDiaarinumero(diaari);
        return mapper.mapAsList(opsit, type);
    }

    @Override
    public <T extends OpetussuunnitelmaBaseDto> List<T> getPerusteenOpetussuunnitelmat(String diaari, Class<T> type) {
        List<Opetussuunnitelma> opsit = repository.findAllByPerusteDiaarinumero(diaari);
        return mapper.mapAsList(opsit, type);
    }

    @Override
    public JsonNode getOpetussuunnitelmanPeruste(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return eperusteetService.getPerusteSisalto(ops.getPeruste().getId(), JsonNode.class);
    }

    @Override
    public List<OpetussuunnitelmaDto> getOtherOpetussuunnitelmat(Long ktId, Set<KoulutusTyyppi> koulutustyypit) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(kayttajanTietoService.getUserOid());
        Koulutustoimija omaKoulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Kayttajaoikeus> oikeudet = kayttajaoikeusRepository.findAllByKayttaja(kayttaja);
        return oikeudet.stream()
                .map(Kayttajaoikeus::getOpetussuunnitelma)
                .filter(ops -> ops.getKoulutustoimija().getYstavat().contains(omaKoulutustoimija)
                        && omaKoulutustoimija.getYstavat().contains(ops.getKoulutustoimija()))
                .filter(ops -> koulutustyypit == null
                        || CollectionUtils.isEmpty(koulutustyypit)
                        || koulutustyypit.contains(ops.getKoulutustyyppi())
                        || (ops.getPeruste() != null && koulutustyypit.contains(ops.getPeruste().getKoulutustyyppi())))
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(String organisaatioId) {
        return repository.findByKoulutustoimijaOrganisaatio(organisaatioId).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        return dispatcher.get(opsId, NavigationBuilder.class)
                .buildNavigation(ktId, opsId);
    }

    @Override
    public NavigationNodeDto buildNavigationJulkinen(Long ktId, Long opsId) {
        NavigationNodeDto rootNode = buildNavigation(ktId, opsId);
        rootNode.getChildren().add(0, NavigationNodeDto.of(NavigationType.tiedot, null, opsId));
        rootNode.setChildren(rootNode.getChildren().stream()
                .filter(node -> !node.getType().equals(NavigationType.suorituspolut) || CollectionUtils.isNotEmpty(node.getChildren()))
                .collect(Collectors.toList())
        );

        return rootNode;
    }

    @Override
    @Cacheable(
            value= "ops-navigation",
            condition = "#esikatselu == false",
            key = "#opsId"
    )
    public NavigationNodeDto buildNavigationPublic(Long ktId, Long opsId, boolean esikatselu) {
        NavigationNodeDto rootNode = dispatcher.get(opsId, NavigationBuilderPublic.class).buildNavigation(ktId, opsId, esikatselu);
        rootNode.getChildren().add(0, NavigationNodeDto.of(NavigationType.tiedot, null, opsId));
        rootNode.setChildren(rootNode.getChildren().stream()
                .filter(node -> !node.getType().equals(NavigationType.suorituspolut) || CollectionUtils.isNotEmpty(node.getChildren()))
                .collect(Collectors.toList())
        );
        NavigationUtil.asetaNumerointi(repository.findOne(opsId), rootNode);
        return rootNode;
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getPohjat() {
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAllByTyyppiAndTila(OpsTyyppi.POHJA, Tila.JULKAISTU);
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getOphOpsPohjat(Set<KoulutusTyyppi> koulutustyypit) {
        KoulutustoimijaJulkinenDto kt = koulutustoimijaService.getKoulutustoimijaJulkinen(koulutustoimijaService.OPH);
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findByKoulutustoimijaIdAndTilaAndTyyppiPerusteKoulutustyyppiIn(kt.getId(), koulutustyypit, OpsTyyppi.OPSPOHJA, Sets.newHashSet(Tila.VALMIS, Tila.JULKAISTU));
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getPohjat(Long ktId, Set<Tila> tilat, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi) {
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findByKoulutustoimijaIdAndTilaAndTyyppiPerusteKoulutustyyppiIn(ktId, koulutustyypit, opsTyyppi, tilat);
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    private void alustaAmmatillinenOpetussuunnitelma(Opetussuunnitelma ops, SisaltoViite rootTkv) {

        // Lisätään suorituspoluille oma sisältöviite
        if (ops.getTyyppi() == OpsTyyppi.OPS) {
            SisaltoViite suorituspolut = new SisaltoViite();
            TekstiKappale tk = new TekstiKappale();
            Map<Kieli, String> suorituspolutTekstikappale = new HashMap<>();
            for (Kieli kieli : Kieli.values()) {
                suorituspolutTekstikappale.put(kieli, messages.translate("suorituspolut", kieli));
            }
            tk.setNimi(LokalisoituTeksti.of(suorituspolutTekstikappale));
            tk.setValmis(true);
            suorituspolut.setTekstiKappale(tkRepository.save(tk));
            suorituspolut.setPakollinen(true);
            suorituspolut.setTyyppi(SisaltoTyyppi.SUORITUSPOLUT);
            suorituspolut.setOwner(ops);
            suorituspolut.setLiikkumaton(false);
            suorituspolut.setVanhempi(rootTkv);
            rootTkv.getLapset().add(tkvRepository.save(suorituspolut));
        }

        // Lisätään tutkinnonosille oma sisältöviite

        SisaltoViite tosat = new SisaltoViite();
        TekstiKappale tk = new TekstiKappale();

        // Haetaan perusteen koulutustyyppi
        CachedPeruste cperuste = ops.getPeruste();
        String koulutustyppi;
        try {
            JsonNode node = objMapper.readTree(cperuste.getPeruste());
            JsonNode koulutustyyppi = node.get("koulutustyyppi");
            koulutustyppi = koulutustyyppi.asText();
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        // Luodaan tutkinnon osat tekstikappale
        Map<Kieli, String> tutkinnonOsatTekstikappale = new HashMap<>();
        for (Kieli kieli : Kieli.values()) {
            if (KoulutusTyyppi.of(koulutustyppi).isValmaTelma()) {
                tutkinnonOsatTekstikappale.put(kieli, messages.translate("koulutuksen-osat", kieli));
            } else {
                tutkinnonOsatTekstikappale.put(kieli, messages.translate("tutkinnon-osat", kieli));
            }
        }
        tk.setNimi(LokalisoituTeksti.of(tutkinnonOsatTekstikappale));

        tk.setValmis(true);
        tosat.setTekstiKappale(tkRepository.save(tk));
        tosat.setLiikkumaton(false);
        tosat.setVanhempi(rootTkv);
        tosat.setPakollinen(true);
        tosat.setTyyppi(SisaltoTyyppi.TUTKINNONOSAT);
        tosat.setOwner(ops);
        rootTkv.getLapset().add(tkvRepository.save(tosat));
    }

    private Opetussuunnitelma findOps(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);

        if (!ops.getKoulutustoimija().getId().equals(kt.getId()) && !areFriends(ops.getKoulutustoimija(), kt)) {
            throw new BusinessRuleViolationException("ops-ei-koulutustoimijan");
        }

        return ops;
    }

    private static boolean areFriends(Koulutustoimija a, Koulutustoimija b) {
        return a.isSalliystavat()
                && b.isSalliystavat()
                && a.getYstavat().contains(b)
                && b.getYstavat().contains(a);
    }

    @Override
    public void setOpsCommon(Long ktId, Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv) {
        setOpsCommon(ops, peruste, rootTkv);
    }

    public void setOpsCommon(Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv) {
        setOpsCommon(ops, peruste, rootTkv, null);
    }

    private void setOpsCommon(Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv, Set<String> tutkinnonOsaKoodiIncludes) {
        if (peruste == null) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }

        Date viimeisinJulkaisu = eperusteetClient.getViimeisinJulkaisuPeruste(peruste.getId());
        CachedPeruste cperuste;
        if (ops.getTyyppi() == OpsTyyppi.YLEINEN) {
            // EP-1392
            // Yhteinen pohja
            cperuste = cachedPerusteRepository.findFirstByPerusteIdAndLuotu(peruste.getId(), viimeisinJulkaisu);
        } else {
            cperuste = cachedPerusteRepository.findOneByDiaarinumeroAndLuotu(peruste.getDiaarinumero(), viimeisinJulkaisu);
        }

        if (cperuste == null) {
            cperuste = new CachedPeruste();
            if (peruste.getNimi() != null) {
                cperuste.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            }

            cperuste.setDiaarinumero(peruste.getDiaarinumero());
            cperuste.setPerusteId(peruste.getId());
            cperuste.setLuotu(viimeisinJulkaisu);
            cperuste.setPeruste(ops.getTyyppi() == OpsTyyppi.YLEINEN
                    ? eperusteetClient.getYleinenPohjaSisalto()
                    : eperusteetClient.getPerusteData(peruste.getId()));
            cperuste.setKoulutustyyppi(peruste.getKoulutustyyppi());
            cperuste.setKoulutuskoodit(peruste.getKoulutukset().stream().map(koulutusDto ->  koulutuskoodiRepository.save(Koulutuskoodi.of(koulutusDto))).collect(Collectors.toSet()));
            cperuste = cachedPerusteRepository.save(cperuste);
        }

        ops.setPeruste(cperuste);

        if (KoulutustyyppiToteutus.AMMATILLINEN.equals(peruste.getToteutus())
                || peruste.getKoulutustyyppi().isValmaTelma()
                || peruste.getToteutus() == null) {
            alustaAmmatillinenOpetussuunnitelma(ops, rootTkv);

            PerusteKaikkiDto perusteSisalto = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class);

            if (perusteSisalto.getTutkinnonOsat() == null) {
                perusteSisalto.setTutkinnonOsat(new ArrayList<>());
            }

            Map<Long, TutkinnonosaKaikkiDto> idToTosaMap = perusteSisalto.getTutkinnonOsat().stream()
                    .collect(Collectors.toMap(TutkinnonosaKaikkiDto::getId, Function.identity()));

            List<TutkinnonosaKaikkiDto> tutkinnonOsat;

            if (ops.getTyyppi() == OpsTyyppi.YLEINEN) {
                tutkinnonOsat = perusteSisalto.getTutkinnonOsat();
            } else {
                tutkinnonOsat = perusteSisalto.getSuoritustavat().stream()
                        .filter(st -> st.getSuoritustapakoodi() == Suoritustapakoodi.of(ops.getSuoritustapa()))
                        .map(st -> st.getTutkinnonOsat().stream())
                        .reduce(Stream::concat)
                        .get()
                        .sorted((a, b) -> {
                            if (a.getJarjestys() == null && b.getJarjestys() == null) {
                                return 0;
                            }
                            if (a.getJarjestys() == null) {
                                return -1;
                            }
                            if (b.getJarjestys() == null) {
                                return 1;
                            }
                            return a.getJarjestys() > b.getJarjestys()
                                    ? 1
                                    : -1;
                        })
                        .map(tosa -> idToTosaMap.get(tosa.getTutkinnonOsa()))
                        .filter(Objects::nonNull)
                        .filter(tutkinnonosa -> CollectionUtils.isEmpty(tutkinnonOsaKoodiIncludes) || tutkinnonOsaKoodiIncludes.contains(tutkinnonosa.getKoodiUri()))
                        .collect(Collectors.toList());
            }

            SisaltoViite tosat = rootTkv.getLapset().stream().filter(sv -> sv.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT)).findFirst().orElseThrow();
            for (TutkinnonosaKaikkiDto tosa : tutkinnonOsat) {
                SisaltoViite uusi = OpetussuunnitelmaSisaltoCreateUtil.perusteenTutkinnonosaToSisaltoviite(tosat, tosa);
                tkvRepository.save(uusi);
            }
        }

        if (KoulutustyyppiToteutus.VAPAASIVISTYSTYO.equals(peruste.getToteutus())
                || KoulutustyyppiToteutus.TUTKINTOONVALMENTAVA.equals(peruste.getToteutus())
                || KoulutustyyppiToteutus.KOTOUTUMISKOULUTUS.equals(peruste.getToteutus())) {
            PerusteKaikkiDto perusteSisalto = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class);
            for (PerusteenOsaViiteDto.Laaja lapsi : perusteSisalto.getSisalto().getLapset()) {
                opetussuunnitelmaSisaltoCreateService.alustaOpetussuunnitelmaPerusteenSisallolla(ops, rootTkv, lapsi);
            }
        }

    }

    @Override
    public List<VanhentunutPohjaperusteDto> haePaivitystaVaativatPerusteet(Long ktId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimijaAndTyyppi(kt, OpsTyyppi.OPS);
        return opsit.stream()
                .filter(ops -> ops.getPeruste() != null && ops.getPeruste().getKoulutustyyppi() != null && ops.getPeruste().getKoulutustyyppi().isAmmatillinen())
                .map(this::getOpetussuunnitelmaVanhentunutPeruste)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private VanhentunutPohjaperusteDto getOpetussuunnitelmaVanhentunutPeruste(Opetussuunnitelma ops) {
        VanhentunutPohjaperusteDto vanhentunutPohjaperusteDto = null;

        if (ops.getPeruste() != null) {
            try {
                Date viimeisinJulkaisu = eperusteetClient.getViimeisinJulkaisuPeruste(ops.getPeruste().getPerusteId());
                CachedPeruste cperuste = ops.getPeruste();
                if (cperuste != null && viimeisinJulkaisu.getTime() > cperuste.getLuotu().getTime()) {
                    PerusteDto perusteDto = eperusteetClient.getPerusteOrNull(ops.getPeruste().getPerusteId(), PerusteDto.class);
                    if (perusteDto != null) {
                        PerusteDto perusteVanha = eperusteetService.getPerusteSisalto(cperuste.getId(), PerusteDto.class);
                        vanhentunutPohjaperusteDto = new VanhentunutPohjaperusteDto();
                        vanhentunutPohjaperusteDto.setOpetussuunnitelma(mapper.map(ops, OpetussuunnitelmaBaseDto.class));
                        vanhentunutPohjaperusteDto.setPerusteVanha(perusteVanha);
                        vanhentunutPohjaperusteDto.setPerusteUusi(perusteDto);
                        vanhentunutPohjaperusteDto.setPerustePaivittynyt(viimeisinJulkaisu);
                        vanhentunutPohjaperusteDto.setEdellinenPaivitys(cperuste.getLuotu());
                    }
                }
            } catch (BusinessRuleViolationException e) {
                log.error("Perusteen viimeisimman julkaisuajan haku epaonnistui: {}", ops.getPeruste().getId());
            }
        }

        return vanhentunutPohjaperusteDto;
    }

    @Override
    public VanhentunutPohjaperusteDto haePaivitystaVaativaPeruste(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return getOpetussuunnitelmaVanhentunutPeruste(ops);
    }

    @Override
    public void paivitaPeruste(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.getOne(opsId);
        PerusteKaikkiDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteKaikkiDto.class);
        CachedPerusteBaseDto cp = eperusteetService.getCachedPeruste(perusteDto);
        CachedPeruste newCachedPeruste = cachedPerusteRepository.findOne(cp.getId());
        cacheManager.getCache("perusteKaikki").evictIfPresent(newCachedPeruste.getId());
        dispatcher.get(ops.getOpsKoulutustyyppi(), OpetussuunnitelmaPerustePaivitysService.class).paivitaOpetussuunnitelma(opsId, perusteDto);

        List<SisaltoViite> tutkinnonOsaViitteet = sisaltoviiteRepository.findTutkinnonosat(ops);
        Set<Long> tutkinnonOsienPerusteIdt = tutkinnonOsaViitteet.stream()
                .filter(tov -> tov.getPerusteId() != null)
                .map(tov -> tov.getPerusteId())
                .collect(Collectors.toSet());
        tutkinnonOsienPerusteIdt.forEach(perusteId -> {
            PerusteDto tosanPeruste = eperusteetClient.getPeruste(perusteId, PerusteDto.class);
            CachedPerusteBaseDto tosanCp = eperusteetService.getCachedPeruste(tosanPeruste);
            CachedPeruste tosanNewCachedPeruste = cachedPerusteRepository.findOne(tosanCp.getId());
            tutkinnonOsaViitteet.stream()
                    .filter(tov -> tov.getPerusteId() != null && tov.getPerusteId().equals(perusteId))
                    .forEach(tosa -> {
                        if (tosa.getTosa() != null && tosa.getTosa().getVierastutkinnonosa() != null) {
                            tosa.getTosa().getVierastutkinnonosa().setCperuste(tosanNewCachedPeruste);
                        } else {
                            tosa.setPeruste(tosanNewCachedPeruste);
                        }
                    });
        });

        ops.setPerustePaivitettyPvm(new Date());
        ops.setPeruste(newCachedPeruste);

        muokkausTietoService.addOpsMuokkausTieto(ops.getId(), ops, MuokkausTapahtuma.PAIVITYS, "peruste-rakenne-paivitys");
    }

    @Override
    @Transactional
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(Long ktId, OpetussuunnitelmaLuontiDto opsDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops;

        if (opsDto.getOpsId() != null) {
            Opetussuunnitelma pohja = repository.findOne(opsDto.getOpsId());
            OpetussuunnitelmaBaseDto dispatchedOps = dispatcher.get(opsDto.getKoulutustyyppi(), opsDto.getTyyppi(), OpetussuunnitelmaCreateService.class).create(kt, opsDto);
            if (dispatchedOps != null) {
                opetussuunnitelmaLisaysMuokkausTapahtuma(dispatchedOps.getId(), pohja);
                return dispatchedOps;
            }

            boolean ophOpsPohja = pohja != null && pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA) && pohja.getKoulutustoimija().getOrganisaatio().equals(KoulutustoimijaService.OPH);
            if (pohja == null || (!ophOpsPohja && !Objects.equals(pohja.getKoulutustoimija().getId(), ktId))) {
                throw new BusinessRuleViolationException("ei-oikeutta-opetussuunnitelmaan");
            }

            ops = pohja.copy();
            if (opsDto.getNimi() != null) {
                ops.setNimi(mapper.map(opsDto.getNimi(), LokalisoituTeksti.class));
            }

            if (pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA)) {
                ops.setTyyppi(OpsTyyppi.OPS);
                ops.changeKoulutustoimija(kt);
                ops.setPohja(pohja);
            }

            ops = repository.save(ops);
            opetussuunnitelmaLisaysMuokkausTapahtuma(ops.getId(), pohja);

            SisaltoViite pohjaRoot = tkvRepository.findOneRoot(pohja);
            SisaltoViite root = tkvService.kopioiHierarkia(
                    pohjaRoot,
                    ops,
                    Collections.singletonMap(SisaltoTyyppi.TUTKINNONOSA, opsDto.getTutkinnonOsaKoodiIncludes()),
                    pohja.getTyyppi().equals(OpsTyyppi.OPSPOHJA) ? SisaltoViite.TekstiHierarkiaKopiointiToiminto.POHJAVIITE : SisaltoViite.TekstiHierarkiaKopiointiToiminto.KOPIOI);
            tkvRepository.save(root);
        }
        else {

            OpetussuunnitelmaBaseDto dispatchedPohja = dispatcher.get(opsDto.getKoulutustyyppi(), OpetussuunnitelmaPohjaCreateService.class).create(kt, opsDto);
            if (dispatchedPohja != null) {
                return dispatchedPohja;
            }

            ops = mapper.map(opsDto, Opetussuunnitelma.class);
            ops.changeKoulutustoimija(kt);
            ops.setTila(Tila.LUONNOS);
            ops = repository.save(ops);

            SisaltoViite rootTkv = null;
            if (opsDto.getTyyppi() != OpsTyyppi.YHTEINEN) {
                rootTkv = new SisaltoViite();
                rootTkv.setOwner(ops);
                ops.getSisaltoviitteet().add(rootTkv);
                rootTkv = tkvRepository.save(rootTkv);
            }

            if (kt.isOph()) {
                opsDto.setTyyppi(OpsTyyppi.POHJA);
                opsDto.setSuoritustapa("pohja");
            }
            else {
                switch (opsDto.getTyyppi()) {
                    case OPS:
                        if (opsDto.getPerusteId() != null) {
                            PerusteDto peruste = eperusteetClient.getPeruste(opsDto.getPerusteId(), PerusteDto.class);
                            setOpsCommon(ops, peruste, rootTkv, opsDto.getTutkinnonOsaKoodiIncludes());
                        }
                        break;
                    case YLEINEN:
                        PerusteDto yleinen = eperusteetClient.getYleinenPohja();
                        setOpsCommon(ops, yleinen, rootTkv);
                        opsDto.setSuoritustapa("yleinen");
                        break;
                    case POHJA:
                        throw new BusinessRuleViolationException("ainoastaan-oph-voi-tehda-pohjia");
                    case OPSPOHJA:
                        break;
                    default:
                        throw new BusinessRuleViolationException("opstyypille-ei-toteutusta");
                }
            }
        }

        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    private void opetussuunnitelmaLisaysMuokkausTapahtuma(Long opsId, Opetussuunnitelma pohja) {
        if (pohja.getTyyppi().equals(OpsTyyppi.OPS)) {
            muokkausTietoService.addOpsMuokkausTieto(opsId, pohja, MuokkausTapahtuma.LUONTI, "opetussuunnitelma-luotu-toisesta-opetussuunnitelmasta");
        }
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaId(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loydy");
        }
        return mapper.map(ops.getKoulutustoimija(), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(Long ktId, Long opsId) {
        return getOpetussuunnitelma(ktId, opsId, OpetussuunnitelmaDto.class);
    }

    @Override
    public <T extends OpetussuunnitelmaBaseDto> T getOpetussuunnitelma(Long ktId, Long opsId, Class<T> type) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return mapper.map(ops, type);
    }

    @Override
    public OpetussuunnitelmaBaseDto getOpetussuunnitelmaPohjaKevyt(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        if (ops.getPohja() != null) {
            Opetussuunnitelma pohja = repository.findOne(ops.getPohja().getId());
            return mapper.map(pohja, OpetussuunnitelmaBaseDto.class);
        }

        return null;
    }

    @Override
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        SisaltoViiteExportDto sisalto = tkvService.getSisaltoRoot(ktId, opsId, SisaltoViiteExportDto.class);
        List<SisaltoViiteExportDto> tutkinnonOsat = tkvService.getTutkinnonOsaViitteet(ktId, opsId, SisaltoViiteExportDto.class);
        OpetussuunnitelmaKaikkiDto result = mapper.map(ops, OpetussuunnitelmaKaikkiDto.class);

        CollectionUtil.treeToStream(sisalto.getLapset(), SisaltoViiteExportDto::getLapset)
                .filter(sisaltoViite -> sisaltoViite.getPerusteenOsaId() != null)
                .forEach(sisaltoViite -> {
                    PerusteenOsaDto perusteenOsaDto = eperusteetService.getPerusteenOsa(ops.getPeruste().getId(), sisaltoViite.getPerusteenOsaId());
                    if (perusteenOsaDto != null) {
                        sisaltoViite.setPerusteSisalto(perusteenOsaDto);
                    }
                });

        result.setSisalto(sisalto);
        result.setTutkinnonOsat(tutkinnonOsat);

        result.setOpintokokonaisuudet(mapper.mapAsList(tkvService.getSisaltoviitteet(ktId, opsId, SisaltoTyyppi.OPINTOKOKONAISUUS), SisaltoViiteExportOpintokokonaisuusDto.class));

        if (ops.getSuoritustapa() != null) {
            List<SuorituspolkuRakenneDto> suorituspolut = tkvService.getSuorituspolkurakenne(ktId, opsId);
            result.setSuorituspolut(suorituspolut);
        }

        return result;
    }

    @Override
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(Long ktId, Long opsId, boolean esikatselu) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        if (ops == null || ops.getTila().equals(Tila.POISTETTU) || (esikatselu && !ops.isEsikatseltavissa())) {
            throw new NotExistsException("");
        }

        if (esikatselu) {
            return getOpetussuunnitelmaKaikki(ktId, opsId);
        }

        return getOpetussuunnitelmaJulkaistuSisalto(opsId);
    }

    @Override
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        Julkaisu julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);

        if (julkaisu == null) {
            return null;
        }

        ObjectNode data = julkaisu.getData().getData();
        try {
            ObjectMapper objectMapper = InitJacksonConverter.createMapper();
            OpetussuunnitelmaKaikkiDto kaikkiDto = objectMapper.treeToValue(data, OpetussuunnitelmaKaikkiDto.class);
            kaikkiDto.setTila(Tila.JULKAISTU);
            return kaikkiDto;
        } catch (JsonProcessingException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new BusinessRuleViolationException("opetussuunnitelman-haku-epaonnistui");
        }
    }

    @Override
    public List<PoistettuDto> getPoistetut(Long ktId, Long id) {
        return new ArrayList<>();
    }

    @Override
    public OpetussuunnitelmaDto update(Long ktId, Long opsId, OpetussuunnitelmaDto body) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        body.setId(opsId);
        body.setTila(ops.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        Opetussuunnitelma updated = mapper.map(body, ops);
        repository.save(updated);
        muokkausTietoService.addOpsMuokkausTieto(opsId, updated, MuokkausTapahtuma.PAIVITYS);
        return mapper.map(updated, OpetussuunnitelmaDto.class);
    }

    @Override
    public OpetussuunnitelmaDto revertTo(Long ktId, Long opsId, Integer revId) {
        OpetussuunnitelmaDto rev = (OpetussuunnitelmaDto) getData(ktId, opsId, revId);
        return update(ktId, opsId, rev);
    }

    @Override
    public KayttajaoikeusDto updateOikeus(Long ktId, Long opsId, Long kayttajaId, KayttajaoikeusDto oikeusDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);

        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOneByKayttajaAndOpetussuunnitelma(kayttaja, ops);

        if (oikeusDto.getOikeus() == KayttajaoikeusTyyppi.ESTETTY) {
            if (oikeus != null) {
                kayttajaoikeusRepository.delete(oikeus);
            }
            return null;
        }

        if (oikeus == null) {
            oikeus = new Kayttajaoikeus();
            oikeus.setKoulutustoimija(kt);
            oikeus.setKayttaja(kayttaja);
            oikeus.setOpetussuunnitelma(ops);
        }

        oikeus.setOikeus(oikeusDto.getOikeus());
        oikeus = kayttajaoikeusRepository.save(oikeus);
        return mapper.map(oikeus, KayttajaoikeusDto.class);
    }

    @Override
    public List<KayttajaoikeusDto> getOikeudet(Long ktId, Long opsId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        return mapper.mapAsList(
                kayttajaoikeusRepository.findAllByKoulutustoimijaAndOpetussuunnitelma(kt, ops),
                KayttajaoikeusDto.class
        );
    }

    @Override
    public List<Revision> getRevisions(Long ktId, Long opsId) {
        return repository.getRevisions(opsId);
    }

    @Override
    public Revision getLatestRevision(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

    @Override
    public Integer getLatestRevisionId(Long ktId, Long opsId) {
        return repository.getLatestRevisionId(opsId);
    }

    @Override
    public Object getData(Long ktId, Long opsId, Integer rev) {
        return mapper.map(repository.findRevision(opsId, rev), OpetussuunnitelmaDto.class);
    }

    @Override
    public Revision getRemoved(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

    @Override
    public OpetussuunnitelmaBaseDto updateTila(Long ktId, Long opsId, Tila tila, boolean generatePDF) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        Tila nykyinen = ops.getTila();
        if (nykyinen.mahdollisetSiirtymat().contains(tila)
                && (ops.getOpsKoulutustyyppi() == null || ops.getOpsKoulutustyyppi().isAmmatillinen()
                || (!(tila.equals(Tila.POISTETTU) && CollectionUtils.isNotEmpty(ops.getJulkaisut())) || permissionManager.hasOphAdminPermission()))) {

            // Muutetaan tila
            ops.setTila(tila);

            // Julkaisun rutiinit
            if (tila.equals(Tila.JULKAISTU)) {

                validoi(ktId, opsId).forEach(Validointi::tuomitse);

                julkaisuService.teeJulkaisu(
                        ktId, opsId,
                        JulkaisuBaseDto
                                .builder()
                                .tiedote(LokalisoituTekstiDto.of("Julkaisu"))
                                .build());
            }
        } else {
            throw new IllegalArgumentException(tila + " ei ole kelvollinen tilasiirtymä");
        }

        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<Validointi> validoi(Long ktId, Long opsId) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        if (ops.getKoulutustyyppi() != null || ops.getPeruste() != null) {
            List<Validointi> validointi = dispatcher.get(
                    ops.getKoulutustyyppi() != null ? ops.getKoulutustyyppi() : ops.getPeruste().getKoulutustyyppi(),
                    OpetussuunnitelmaValidationService.class).validoi(ktId, opsId);
            if (validointi != null) {
                return validointi;
            }
        }

        return validointiService.validoi(ops);
    }

    @Override
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(PageRequest p, OpetussuunnitelmaQueryDto pquery) {
        return repository.findBy(p, pquery)
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class));
    }

    @Override
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
    public List<OpetussuunnitelmaDto> getKaikkiJulkaistutOpetussuunnitelmat() {
        return julkaisuRepository.findAllJulkaistutOpetussuunnitelmatByVoimassaolo(DateTime.now().getMillis(), true, true, false).stream()
                .map(this::convertToOpetussuunnitelmaDto).collect(Collectors.toList());
    }

    private OpetussuunnitelmaDto convertToOpetussuunnitelmaDto(String obj) {
        try {
            return objMapper.readValue(obj, OpetussuunnitelmaDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
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

    private List<fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto> recursiveOpintokokonaisuudet(SisaltoViiteExportDto sisalto) {
        List<fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto> opintokokonaisuudet = new ArrayList<>();

        if (sisalto.getOpintokokonaisuus() != null) {
            opintokokonaisuudet.add(sisalto.getOpintokokonaisuus());
        }

        if (CollectionUtils.isNotEmpty(sisalto.getLapset())) {
            sisalto.getLapset().forEach(lapsi -> opintokokonaisuudet.addAll(recursiveOpintokokonaisuudet(lapsi)));
        }

        return opintokokonaisuudet;
    }

    @Override
    public OpetussuunnitelmaDto updateKoulutustoimija(Long ktId, Long opsId, KoulutustoimijaBaseDto body) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        if (!ops.getKoulutustoimija().getId().equals(ktId)) {
            throw new BusinessRuleViolationException("vain-oman-opetussuunnitelman-voi-siirtaa");
        }

        if (koulutustoimijaService.getOmatYstavat(ktId).stream()
                .noneMatch(ystava -> ystava.getId().equals(body.getId()))) {
            throw new BusinessRuleViolationException("siirto-mahdollinen-vain-ystavaorganisaatiolle");
        }

        Koulutustoimija kt = koulutustoimijaRepository.findOne(body.getId());
        ops.changeKoulutustoimija(kt);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public void updateOpetussuunnitelmaSisaltoviitePiilotukset() {

        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAll();

        opetussuunnitelmat.forEach(opetussuunnitelma -> {
            List<SisaltoViite> polut = sisaltoviiteRepository.findSuorituspolut(opetussuunnitelma);
            List<SisaltoViiteDto> sisaltoviitteet = mapper.mapAsList(polut, SisaltoViiteDto.class);
            log.info("Päivitetään tutkintonimikkeet ja osaamisalat: {}", opetussuunnitelma.getId());
            sisaltoviitteet.forEach(sisaltoviite -> {
                try {
                    tkvService.updateOpetussuunnitelmaPiilotetutSisaltoviitteet(sisaltoviite, opetussuunnitelma);
                }
                catch (BusinessRuleViolationException ex) {
                    log.error(ex.getLocalizedMessage());
                }
            });
        });
    }

    @Override
    public OpetussuunnitelmaDto updateKoulutustoimijaPassivoidusta(Long ktId, Long opsId) {

        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        List<OrganisaatioHistoriaLiitosDto> historiaLiitosOrganisaatiot = organisaatioService
                .getOrganisaationHistoriaLiitokset(kt.getOrganisaatio());

        if (historiaLiitosOrganisaatiot.stream()
                .filter(historialiitos -> historialiitos.getOrganisaatio().getStatus().equals(OrganisaatioStatus.PASSIIVINEN)
                        && historialiitos.getOrganisaatio().getOid().equals(ops.getKoulutustoimija().getOrganisaatio()))
                .collect(Collectors.toList()).isEmpty()) {
            throw new BusinessRuleViolationException("siirto-mahdollinen-aiemmin-passivoidulta-organisaatiolta");
        }

        ops.changeKoulutustoimija(kt);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getJulkaistuSisaltoObjectNode(Long opetussuunnitelmaId, List<String> queryList) {
        Opetussuunnitelma opetussuunnitelma = repository.findOne(opetussuunnitelmaId);

        if (opetussuunnitelma == null || opetussuunnitelma.getTila().equals(Tila.POISTETTU)) {
            throw new NotExistsException("");
        }

        String query = queryList.stream().reduce("$", (subquery, element) -> {
            if (NumberUtils.isCreatable(element)) {
                return subquery + String.format("?(@.id==%s)", element);
            }
            return subquery + "." + element;
        });

        try {
            return objMapper.readValue(julkaisuRepository.findJulkaisutByJsonPath(opetussuunnitelmaId, query), Object.class);
        } catch (JsonProcessingException e) {
            log.error(Throwables.getStackTraceAsString(e));
            return null;
        }
    }
}
