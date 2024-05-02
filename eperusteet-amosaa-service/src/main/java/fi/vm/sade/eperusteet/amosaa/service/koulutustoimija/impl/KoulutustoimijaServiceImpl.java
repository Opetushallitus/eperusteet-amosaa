package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YstavaStatus;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.vm.sade.eperusteet.amosaa.service.security.KoulutustyyppiRolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;

@Service
@Transactional
public class KoulutustoimijaServiceImpl implements KoulutustoimijaService {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutustoimijaServiceImpl.class);

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutustoimijaRepository repository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    @Lazy
    private MaintenanceService maintenanceService;

    @Autowired
    private KoodistoClient koodistoClient;

    @Autowired
    private DtoMapper mapper;

    @PersistenceContext
    private EntityManager em;

    private static String OPH = "1.2.246.562.10.00000000001";

    @Transactional
    private Koulutustoimija initialize(String kOid) {
        Koulutustoimija koulutustoimija = repository.findOneByOrganisaatio(kOid);
        if (koulutustoimija != null) {
            return koulutustoimija;
        }
        return createKoulutustoimija(kOid);
    }

    private Koulutustoimija createKoulutustoimija(String kOid) {
        LOG.debug("Luodaan uusi organisaatiota vastaava koulutustoimija ensimmäistä kertaa", kOid);
        JsonNode organisaatio = organisaatioService.getOrganisaatio(kOid);
        if (organisaatio == null) {
            return null;
        }

        Koulutustoimija uusiKoulutustoimija = new Koulutustoimija();
        uusiKoulutustoimija.setNimi(LokalisoituTeksti.of(organisaatio.get("nimi")));
        uusiKoulutustoimija.setOrganisaatio(kOid);

        if (!ObjectUtils.isEmpty(organisaatio.get("oppilaitosTyyppiUri"))) {
            uusiKoulutustoimija.setOppilaitosTyyppiKoodiUri(organisaatio.get("oppilaitosTyyppiUri").asText().replaceAll("#[0-9]+", ""));
        }

        if (organisaatio.get("tyypit") != null && organisaatio.get("tyypit").isArray()) {
            for (final JsonNode objNode : organisaatio.get("tyypit")) {
                if ("Ryhma".equals(objNode.asText())) {
                    uusiKoulutustoimija.setOrganisaatioRyhma(true);
                }
            }
        }
        uusiKoulutustoimija = repository.save(uusiKoulutustoimija);
        // nollaa cache, jotta lisätty koulutustoimija on jatkossa saatavilla
        maintenanceService.clearCache("koulutustoimijat");
        return uusiKoulutustoimija;
    }

    @Override
    public List<KoulutustoimijaBaseDto> initKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
                .map(this::initialize)
                .filter(Objects::nonNull)
                .map(kt -> mapper.map(kt, KoulutustoimijaBaseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KoulutustoimijaDto updateKoulutustoimija(Long ktId, KoulutustoimijaDto ktDto) {
        Koulutustoimija toimija = repository.findOne(ktId);
        if (ktDto.getYstavat() == null) {
            ktDto.setYstavat(new HashSet<>());
        }
        Koulutustoimija uusi = mapper.map(ktDto, Koulutustoimija.class);
        uusi.getYstavat().remove(toimija);
        toimija.setKuvaus(uusi.getKuvaus());
        toimija.setYstavat(uusi.getYstavat());
        toimija.getYstavat();
        toimija.setSalliystavat(uusi.isSalliystavat());
        return mapper.map(toimija, KoulutustoimijaDto.class);
    }

    @Override
    @Transactional
    public void hylkaaYhteistyopyynto(Long ktId, Long vierasKtId) {
        Koulutustoimija toimija = repository.findOne(ktId);
        Koulutustoimija vierasToimija = repository.findOne(vierasKtId);
        assertExists(vierasToimija, "Pyynnön tehnyttä koulutustoimijaa ei ole olemassa");
        if (vierasToimija.getYstavat() != null && vierasToimija.getYstavat().contains(toimija)) {
            vierasToimija.getYstavat().remove(toimija);
            repository.save(vierasToimija);
        }
        else {
            throw new BusinessRuleViolationException("Koulutustoimija ei ole tehnyt pyyntöä");
        }
    }

    @Override
    @Transactional
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
                .map(ktId -> {
                    LOG.debug("Käyttäjän koulutustoimija", ktId);
                    Koulutustoimija kt = repository.findOneByOrganisaatio(ktId);
                    return mapper.map(kt, KoulutustoimijaBaseDto.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Cacheable("koulutustoimijat")
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat() {
        return repository.findAll().stream()
                .map(koulutustoimija -> mapper.map(koulutustoimija, KoulutustoimijaBaseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(PageRequest page, KoulutustoimijaQueryDto query) {
        return repository.findBy(page, query)
                .map(ops -> mapper.map(ops, KoulutustoimijaJulkinenDto.class));
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(Long ktId) {
        return mapper.map(repository.findOne(ktId), KoulutustoimijaDto.class);
    }

    @Override
    public Long getKoulutustoimija(String idTaiOid) {
        Long result;
        try {
            Koulutustoimija koulutustoimija = repository.findOne(Long.parseLong(idTaiOid));
            result = koulutustoimija.getId();
        } catch (NumberFormatException | NullPointerException ex) {
            result = repository.findOneIdByOrganisaatio(idTaiOid);
        }
        return result;
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(Long ktId) {
        return mapper.map(repository.findOne(ktId), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(String ktOid) {
        return mapper.map(repository.findOneByOrganisaatio(ktOid), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public <T> List<T> getPaikallisetTutkinnonOsat(Long ktId, Class<T> tyyppi) {
        Koulutustoimija kt = repository.findOne(ktId);
        return mapper.mapAsList(sisaltoviiteRepository.findAllPaikallisetTutkinnonOsat(kt), tyyppi);
    }

    private <T> Predicate<T> distinctBy(Function<T, ?> identity) {
        Set<Object> filter = new HashSet<>();
        return v -> filter.add(identity.apply(v));
    }

    @Override
    public List<KoulutustoimijaYstavaDto> getOrganisaatioHierarkiaYstavat(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        OrganisaatioHierarkiaDto root = getOrganisaatioHierarkia(ktId);

        if (root == null) {
            return new ArrayList<>();
        }
        else {
            return root.getAll()
                    .filter(oh -> oh.getOid().equals(kt.getOrganisaatio()))
                    .findFirst()
                    .map(self -> Stream.concat(
                        self.getAll().map(OrganisaatioHierarkiaDto::getOid),
                        self.getParentOidPath() == null
                                ? Stream.empty()
                                : Arrays.stream(self.getParentOidPath().split("/")))
                        .distinct()
                        .filter(org -> !org.equals(OPH))
                        .filter(org -> !org.equals(kt.getOrganisaatio()))
                        .map(repository::findOneByOrganisaatio)
                        .filter(Objects::nonNull)
                        .map(ykt -> mapper.map(ykt, KoulutustoimijaYstavaDto.class))
                        .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
        }
    }

    @Override
    public List<KoulutustoimijaYstavaDto> getOmatYstavat(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        Stream<KoulutustoimijaYstavaDto> hierarkiaYstavat = getOrganisaatioHierarkiaYstavat(ktId).stream()
                    .peek(ystava -> ystava.setStatus(YstavaStatus.YHTEISTYO));
        Stream<KoulutustoimijaYstavaDto> lisatytYstavat = kt.getYstavat().stream()
                    .map(ystava -> {
                        KoulutustoimijaYstavaDto ystavaDto = mapper.map(ystava, KoulutustoimijaYstavaDto.class);
                        ystavaDto.setStatus(ystava.getYstavat() != null && ystava.getYstavat().contains(kt)
                                ? YstavaStatus.YHTEISTYO
                                : YstavaStatus.ODOTETAAN);
                        return ystavaDto;
                    });

        HashSet<Long> filterHelper = new HashSet<>();
        filterHelper.add(ktId);
        List<KoulutustoimijaYstavaDto> result = Stream.concat(hierarkiaYstavat, lisatytYstavat)
                .filter(org -> filterHelper.add(org.getId()))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public OrganisaatioHierarkiaDto getOrganisaatioHierarkia(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        OrganisaatioHierarkiaDto result = organisaatioService.getOrganisaatioPuu(kt.getOrganisaatio());
        return result;
    }

    @Override
    public List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(Long ktId) {
        return mapper.mapAsList(repository.findAllYstavalliset(), KoulutustoimijaBaseDto.class);
    }

    @Override
    public List<KoulutustoimijaBaseDto> getPyynnot(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        Set<Koulutustoimija> pyynnot = repository.findAllYstavaPyynnotForKoulutustoimija(kt);
        pyynnot.removeAll(kt.getYstavat());
        return mapper.mapAsList(pyynnot, KoulutustoimijaBaseDto.class);
    }

    @Override
    public List<OrganisaatioHistoriaLiitosDto> getOrganisaatioHierarkiaHistoriaLiitokset(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        return organisaatioService.getOrganisaationHistoriaLiitokset(kt.getOrganisaatio());
    }

    @Override
    public EtusivuDto getEtusivu(@P("ktId") Long ktId, List<KoulutusTyyppi> koulutustyypit) {
        Koulutustoimija koulutustoimija = repository.findOne(ktId);
        EtusivuDto result = new EtusivuDto();

        if (koulutustoimija != null) {
            Set<Koulutustoimija> koulutustoimijat = Collections.singleton(koulutustoimija);
            if (koulutustyypit.stream().anyMatch(kt -> SecurityUtil.isUserOphAdmin(KoulutustyyppiRolePrefix.of(kt)))) {
                koulutustoimijat = null;
            }

            result.setToteutussuunnitelmatKeskeneraiset(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.OPS, false, koulutustoimijat, koulutustyypit));
            result.setToteutussuunnitelmatJulkaistut(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.OPS, true, koulutustoimijat, koulutustyypit));

            if (koulutustyypit.contains(KoulutusTyyppi.VAPAASIVISTYSTYO) || koulutustyypit.contains(KoulutusTyyppi.TUTKINTOONVALMENTAVA)) {
                result.setToteutussuunnitelmaPohjatKeskeneraiset(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.OPSPOHJA, Collections.singleton(Tila.LUONNOS), koulutustoimijat, koulutustyypit));
                result.setToteutussuunnitelmaPohjatValmiit(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.OPSPOHJA, Collections.singleton(Tila.VALMIS), koulutustoimijat, koulutustyypit));
            } else if (koulutustoimija.isOph()) {
                result.setKtYhteinenOsuusKeskeneraiset(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.POHJA, false, koulutustoimijat));
                result.setKtYhteinenOsuusJulkaistut(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.POHJA, true, koulutustoimijat));
            } else {
                result.setKtYhteinenOsuusKeskeneraiset(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.YHTEINEN, false, koulutustoimijat));
                result.setKtYhteinenOsuusJulkaistut(opetussuunnitelmaRepository.countByTyyppi(OpsTyyppi.YHTEINEN, true, koulutustoimijat));
            }

        }

        return result;
    }

    @Override
    public List<KoulutustoimijaJulkinenDto> findKoulutusatyypinKoulutustoimijat(Set<KoulutusTyyppi> koulutustyypit) {
        return mapper.mapAsList(repository.findByKoulutustyypit(koulutustyypit), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public List<KoodistoKoodiDto> getVstYksilollisetOppilaitostyypit() {
        Set<String> yksilollisetKoodit = opetussuunnitelmaRepository.findDistinctOppilaitosTyyppiKoodiUri(Set.of(KoulutusTyyppi.VAPAASIVISTYSTYO, KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO));
        return yksilollisetKoodit.stream().map(koodi -> koodistoClient.getByUri(koodi)).collect(Collectors.toList());
    }

}
