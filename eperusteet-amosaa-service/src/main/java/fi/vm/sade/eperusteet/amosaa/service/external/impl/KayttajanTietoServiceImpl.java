package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaKtoDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.utils.client.OphClientHelper;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.amosaa.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
@Transactional
public class KayttajanTietoServiceImpl implements KayttajanTietoService {
    @Autowired
    private KayttajaClient client;

    @Lazy
    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KayttooikeusService kayttooikeusService;

    @Autowired
    OphClientHelper ophClientHelper;

    @Value("${cas.service.oppijanumerorekisteri-service:''}")
    private String onrServiceUrl;

    private static final String HENKILO_API = "/henkilo/";
    private static final String HENKILOT_BY_LIST = HENKILO_API + "henkilotByHenkiloOidList";

    @Override
    public KayttajaDto haeKayttajanTiedot() {
        KayttajaDto kayttajaDto = mapper.map(getKayttaja(), KayttajaDto.class);
        KayttajanTietoDto kirjautunutKayttaja = haeKirjautaunutKayttaja();
        return mapper.map(kirjautunutKayttaja, kayttajaDto);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeSuosikki(Long opsId) {
        Kayttaja kayttaja = getKayttaja();
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-olemassa");
        }
        kayttaja.getSuosikit().remove(ops);
    }

    @Override
    @Transactional(readOnly = false)
    public void addSuosikki(Long opsId) {
        Kayttaja kayttaja = getKayttaja();
        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-olemassa");
        }
        kayttaja.getSuosikit().add(ops);
    }

    @Override
    public KayttajanTietoDto hae(String oid) {
        return client.hae(oid);
    }

    @Override
    public KayttajanTietoDto hae(Long id) {
        return client.hae(kayttajaRepository.findOne(id).getOid());
    }

    @Override
    @Async
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Component
    public static class KayttajaClient {

        @Autowired
        private RestClientFactory restClientFactory;

        @Value("${cas.service.oppijanumerorekisteri-service:''}")
        private String onrServiceUrl;

        private static final String HENKILO_API = "/henkilo/";
        private final ObjectMapper mapper = new ObjectMapper();

        @Cacheable("kayttajat")
        public KayttajanTietoDto hae(String oid) {
            OphHttpClient client = restClientFactory.get(onrServiceUrl, true);
            String url = onrServiceUrl + HENKILO_API + oid;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<KayttajanTietoDto>execute(request)
                    .handleErrorStatus(SC_UNAUTHORIZED, SC_FORBIDDEN)
                    .with((res) -> Optional.of(new KayttajanTietoDto(oid)))
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            JsonNode json = mapper.readTree(text);
                            return parsiKayttaja(json);
                        } catch (IOException e) {
                            return new KayttajanTietoDto(oid);
                        }
                    })
                    .orElse(new KayttajanTietoDto(oid));
        }
    }

    @Override
    public String getUserOid() {
        return SecurityUtil.getAuthenticatedPrincipal().getName();
    }

    @Override
    @Cacheable(value = "kayttajatiedot", unless = "#result == null")
    public KayttajanTietoDto haeNimi(Long id) {
        Kayttaja kayttaja = kayttajaRepository.findOne(id);
        return hae(kayttaja.getOid());
    }

    @Override
    public Kayttaja getKayttaja() {
        String oid = SecurityUtil.getAuthenticatedPrincipal().getName();
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja == null) {
            Kayttaja uusi = new Kayttaja();
            uusi.setOid(oid);
            kayttaja = kayttajaRepository.save(uusi);
        }
        return kayttaja;
    }

    @Override
    public KayttajaDto saveKayttaja(String oid) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja == null) {
            Kayttaja uusi = new Kayttaja();
            uusi.setOid(oid);
            kayttaja = kayttajaRepository.save(uusi);
        }

        return mapper.map(kayttaja, KayttajaDto.class);
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        KayttajanTietoDto kayttajatieto = hae(getUserOid());
        if (kayttajatieto == null) {
            // "fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttajatieto = new KayttajanTietoDto(getUserOid());
        }
        return kayttajatieto;
    }

    @Override
    public boolean updateKoulutustoimijat(PermissionEvaluator.RolePrefix rolePrefix) {
        koulutustoimijaService.initKoulutustoimijat(getUserOrganizations(rolePrefix));
        return true;
    }

    @Override
    public List<KoulutustoimijaBaseDto> koulutustoimijat(PermissionEvaluator.RolePrefix rolePrefix) {
        List<KoulutustoimijaBaseDto> koulutustoimijat = SecurityUtil.isUserOphAdmin(rolePrefix) ?
                koulutustoimijaService.getKoulutustoimijat() : koulutustoimijaService.getKoulutustoimijat(getUserOrganizations(rolePrefix));

        return koulutustoimijat.stream()
                .filter(ktDto -> ktDto != null && ktDto.getId() != null)
                .collect(Collectors.toList());
    }

    private List<KayttajaKtoDto> getKayttajat(Koulutustoimija kt, PermissionEvaluator.RolePrefix rolePrefix) {
        if (kt.isOph()) {
            return new ArrayList<>();
        }
        else {
            Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(kt.getId());
            return getOrganisaatioVirkailijatAsKayttajat(Collections.singletonList(koulutustoimija.getOrganisaatio()), rolePrefix);
        }
    }

    @Override
    public List<KayttajaKtoDto> getKayttajat(Long kOid, PermissionEvaluator.RolePrefix rolePrefix) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(kOid);
        return getKayttajat(kt, rolePrefix);
    }

    @Override
    public List<KayttajaKtoDto> getKaikkiKayttajat(Long kOid, PermissionEvaluator.RolePrefix rolePrefix) {
        Map<String, KayttajaKtoDto> result = new HashMap<>();
        Koulutustoimija self = koulutustoimijaRepository.findOne(kOid);
        List<KoulutustoimijaYstavaDto> ystavaorganisaatiot = koulutustoimijaService.getOmatYstavat(self.getId());

        if (!self.isOph()) {
            result.putAll(getOrganisaatioVirkailijatAsKayttajat(Collections.singletonList(self.getOrganisaatio()), rolePrefix).stream()
                    .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja, (kayttaja1, kayttaja2) -> kayttaja1)));
            try {
                result.putAll(getOrganisaatioVirkailijatAsKayttajat(ystavaorganisaatiot.stream()
                        .map(KoulutustoimijaYstavaDto::getOrganisaatio).collect(Collectors.toList()), rolePrefix).stream()
                        .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja, (kayttaja1, kayttaja2) -> kayttaja1)));
            } catch (RuntimeException ignored) {
            }
        }

        return new ArrayList<>(result.values());
    }

    @Override
    public List<KayttajaKtoDto> getYstavaOrganisaatioKayttajat(Long kOid, PermissionEvaluator.RolePrefix rolePrefix) {
        Koulutustoimija self = koulutustoimijaRepository.findOne(kOid);
        List<KoulutustoimijaYstavaDto> ystavaorganisaatiot = koulutustoimijaService.getOmatYstavat(self.getId());
        return getOrganisaatioVirkailijatAsKayttajat(ystavaorganisaatiot.stream().map(KoulutustoimijaYstavaDto::getOrganisaatio).collect(Collectors.toList()), rolePrefix);
    }

    private List<KayttajaKtoDto> getOrganisaatioVirkailijatAsKayttajat(List<String> organisaatioOids, PermissionEvaluator.RolePrefix rolePrefix) {

        if (organisaatioOids.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, KayttajaKtoDto> kayttajaMap = organisaatioOids.stream()
                .flatMap(organisaatioOid -> kayttooikeusService.getOrganisaatioVirkailijat(organisaatioOid, rolePrefix).stream()
                        .map(kayttooikeusKayttajaDto -> mapper.map(kayttooikeusKayttajaDto, KayttajaKtoDto.class).withOrganisaatioOid(organisaatioOid)
                    ))
                .collect(Collectors.toMap(KayttajaDto::getOid, kayttaja -> kayttaja, (kayttaja1, kayttaja2) -> kayttaja1));

        if (!kayttajaMap.isEmpty()) {
            kayttajaRepository.findByOidIn(kayttajaMap.keySet())
                    .forEach(kayttaja -> {
                        if (kayttajaMap.containsKey(kayttaja.getOid())) {
                            kayttajaMap.get(kayttaja.getOid()).setId(kayttaja.getId());
                        }
                    });
        }

        return new ArrayList<>(kayttajaMap.values());
    }

    @Override
    public Set<String> getUserOrganizations(PermissionEvaluator.RolePrefix rolePrefix) {
        return SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class), rolePrefix);
    }

    @Override
    public KayttajanTietoDto getKayttaja(Long koulutustoimijaId, String oid) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(oid);
        if (kayttaja != null) {
            return client.hae(oid);
        }
        return null;
    }

    @Override
    public KayttajanTietoDto getKayttaja(Long koulutustoimijaId, Long id) {
        // FIXME
//        List<KayttajaoikeusTyyppi> oikeudet = kayttajaoikeusRepository.findKoulutustoimijaOikeus(koulutustoimijaId, kayttajaRepository.findOneByOid(oid).getId());
//        if (oikeudet.isEmpty()) {
//            throw new BusinessRuleViolationException("kayttajan-pitaa-kuulua-koulutustoimijaan");
//        }
        return hae(id);
    }

    @Override
    public EtusivuDto haeKayttajanEtusivu(PermissionEvaluator.RolePrefix rolePrefix) {
        EtusivuDto result = new EtusivuDto();
        List<Long> koulutustoimijat = mapper.mapAsList(koulutustoimijat(rolePrefix), Koulutustoimija.class).stream().map(Koulutustoimija::getId).toList();
        if (ObjectUtils.isEmpty(koulutustoimijat)) {
            result.setToteutussuunnitelmatKeskeneraiset(0L);
            result.setToteutussuunnitelmatJulkaistut(0L);
            result.setKtYhteinenOsuusKeskeneraiset(0L);
            result.setKtYhteinenOsuusJulkaistut(0L);
        } else {
            result.setToteutussuunnitelmatKeskeneraiset(opsRepository.countByTyyppi(OpsTyyppi.OPS, Collections.singleton(Tila.LUONNOS), koulutustoimijat));
            result.setToteutussuunnitelmatJulkaistut(opsRepository.countByTyyppi(OpsTyyppi.OPS, Collections.singleton(Tila.JULKAISTU), koulutustoimijat));
            result.setKtYhteinenOsuusKeskeneraiset(opsRepository.countByTyyppi(OpsTyyppi.YHTEINEN, Collections.singleton(Tila.LUONNOS), koulutustoimijat));
            result.setKtYhteinenOsuusJulkaistut(opsRepository.countByTyyppi(OpsTyyppi.YHTEINEN, Collections.singleton(Tila.JULKAISTU), koulutustoimijat));
        }
        return result;
    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return ophClientHelper.postAsList(onrServiceUrl, onrServiceUrl + HENKILOT_BY_LIST, oid, KayttajanTietoDto.class);
    }

}
