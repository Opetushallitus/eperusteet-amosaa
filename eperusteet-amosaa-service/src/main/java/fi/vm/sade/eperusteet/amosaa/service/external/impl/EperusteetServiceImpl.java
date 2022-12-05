/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;

import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

/**
 * @author nkala
 */
@Service
@Transactional(readOnly = true)
public class EperusteetServiceImpl implements EperusteetService {
    private static final Logger logger = LoggerFactory.getLogger(EperusteetServiceImpl.class);

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    EperusteetClient eperusteetClient;

    private ObjectMapper mapper;

    @Autowired
    private DtoMapper dtoMapper;

    @Autowired
    private PerusteCacheService perusteCacheService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;
    
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JsonMapper jsonMapper;

    private RestTemplate client;

    @Autowired
    private HttpEntity httpEntity;

    @PostConstruct
    protected void init() {
        client = new RestTemplate(singletonList(jsonMapper.messageConverter().orElseThrow(IllegalStateException::new)));
        mapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG));
        client.getMessageConverters().add(converter);
    }

    @Override
    public CachedPerusteBaseDto getCachedPeruste(PerusteDto peruste) {
        Date viimeisinJulkaisu = eperusteetClient.getViimeisinJulkaisuPeruste(peruste.getId());
        CachedPeruste cperuste = cachedPerusteRepository.findFirstByPerusteIdAndLuotu(peruste.getId(), viimeisinJulkaisu);
        if (cperuste == null) {
            cperuste = new CachedPeruste();
            if (peruste.getNimi() != null) {
                cperuste.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            }

            cperuste.setDiaarinumero(peruste.getDiaarinumero());
            cperuste.setPerusteId(peruste.getId());
            cperuste.setLuotu(viimeisinJulkaisu);
            cperuste.setPeruste(eperusteetClient.getPerusteData(peruste.getId()));
            cperuste.setKoulutustyyppi(peruste.getKoulutustyyppi());
            cperuste.setKoulutukset(peruste.getKoulutukset());
            cperuste = cachedPerusteRepository.save(cperuste);
        }
        return dtoMapper.map(cperuste, CachedPerusteBaseDto.class);
    }

    @Override
    public JsonNode getTutkinnonOsat(Long id) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            return node.get("tutkinnonOsat");
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat) {
        List<TutkinnonOsaSuoritustapaDto> result = new ArrayList<>();
        for (JsonNode tosaJson : tutkinnonosat) {
            try {
                result.add(mapper.treeToValue(tosaJson, TutkinnonOsaSuoritustapaDto.class));
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(EperusteetServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new BusinessRuleViolationException("tutkinnon-osan-muuttaminen-epaonnistui");
            }
        }
        return result;
    }

    @Override
    public JsonNode getSuoritustavat(Long id) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            return node.get("suoritustavat");
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public List<RakenneModuuliTunnisteDto> getSuoritustavat(Long ktId, Long opetussuunnitelmaId) {
        OpetussuunnitelmaDto opetussuunnitelma = opetussuunnitelmaService.getOpetussuunnitelma(ktId, opetussuunnitelmaId);
        List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSuorituspolut(ktId, opetussuunnitelmaId,
                SisaltoViiteDto.class);
        SuoritustapaLaajaDto suoritustapaLaajaDto = perusteCacheService.getSuoritustapa(opetussuunnitelmaId, opetussuunnitelma.getPeruste().getId());

        return sisaltoviitteet.stream()
                .map(sisaltoviite -> getYksittaisenRakenteenSuoritustavat(suoritustapaLaajaDto, sisaltoviite))
                .collect(Collectors.toList());
    }

    @Override
    public RakenneModuuliTunnisteDto getYksittaisenRakenteenSuoritustavat(SuoritustapaLaajaDto suoritustapaLaajaDto,
            SisaltoViiteDto sisaltoViite) {

        Map<UUID, SuorituspolkuRiviDto> suorituspolkuRiviMap = sisaltoViite.getSuorituspolku().getRivit().stream()
                .filter(suorituspolkuRivi -> !suorituspolkuRivi.getPiilotettu())
                .collect(Collectors.toMap(SuorituspolkuRiviDto::getRakennemoduuli, Function.identity()));
        Set<UUID> piilotetut = sisaltoViite.getSuorituspolku().getRivit().stream()
                .filter(suorituspolkuRivi -> suorituspolkuRivi.getPiilotettu())
                .map(SuorituspolkuRiviDto::getRakennemoduuli)
                .collect(Collectors.toSet());

        Stack<RakenneModuuliDto> originStack = new Stack<>();
        Stack<RakenneModuuliDto> filterStack = new Stack<>();
        originStack.push(suoritustapaLaajaDto.getRakenne());

        RakenneModuuliTunnisteDto masterRakenneModuuliTunnisteDto = RakenneModuuliTunnisteDto.of(
                suoritustapaLaajaDto.getRakenne(),
                suorituspolkuRiviMap.get(suoritustapaLaajaDto.getRakenne().getTunniste()));
        filterStack.push(masterRakenneModuuliTunnisteDto);

        while (!originStack.empty()) {
            RakenneModuuliDto originRakenne = originStack.pop();
            RakenneModuuliDto filterRakenne = filterStack.pop();

            if (originRakenne.getOsat() != null) {
                originRakenne.getOsat().stream()
                        .filter(rakenneOsa -> !piilotetut.contains(rakenneOsa.getTunniste()))
                        .forEach(rakenneOsa -> {

                            SuorituspolkuRiviDto suoritusPolkuRiviDto = suorituspolkuRiviMap.get(rakenneOsa.getTunniste());
                            RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto = RakenneModuuliTunnisteDto.of(rakenneOsa, suoritusPolkuRiviDto);

                            filterRakenne.getOsat().add(rakenneModuuliTunnisteDto);

                            if (rakenneOsa instanceof RakenneModuuliDto) {
                                originStack.push((RakenneModuuliDto) rakenneOsa);
                                filterStack.push(rakenneModuuliTunnisteDto);
                            }
                        });
            }

        }

        return masterRakenneModuuliTunnisteDto;
    }

    @Override
    public JsonNode getTutkinnonOsa(Long id, Long tosaId) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);

        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            for (JsonNode tosa : node.get("tutkinnonOsat")) {
                if (tosaId.equals(tosa.get("id").asLong())) {
                    return tosa;
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        throw new BusinessRuleViolationException("tutkinnon-osa-ei-loytynyt");
    }

    @Override
    public JsonNode getSuoritustapa(Long id, String tyyppi) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            if (node.get("suoritustavat") != null) {
                for (JsonNode suoritustapa : node.get("suoritustavat")) {
                    if (suoritustapa.get("suoritustapakoodi").asText().equals(tyyppi)) {
                        return suoritustapa;
                    }
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        return null;
    }

    private void collectTunnisteet(JsonNode node, Set<UUID> tunnisteet) {
        tunnisteet.add(UUID.fromString(node.get("tunniste").asText()));
        if (node.has("osat")) {
            for (JsonNode osa : node.get("osat")) {
                collectTunnisteet(osa, tunnisteet);
            }
        }
    }

    @Override
    public JsonNode getGeneeriset() {
        String url = eperusteetServiceUrl + "/api/geneerinenarviointi/julkaistu";
        JsonNode result = client.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
        return result;
    }

    @Override
    public Set<UUID> getRakenneTunnisteet(Long id, String suoritustapa) {
        JsonNode st = getSuoritustapa(id, suoritustapa);

        if (st == null) {
            return Collections.emptySet();
        }
        HashSet<UUID> uuids = new HashSet<>();
        collectTunnisteet(st.get("rakenne"), uuids);
        return uuids;
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        return client.exchange(eperusteetServiceUrl + "/api/tiedotteet" + params, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        String uri = eperusteetServiceUrl.concat("/api/tiedotteet/haku").concat(queryDto.toRequestParams());
        return client.exchange(uri, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
    }

    @Override
    public JsonNode getTutkinnonOsaViite(Long id, String tyyppi, Long tosaId) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            for (JsonNode suoritustapa : node.get("suoritustavat")) {
                for (JsonNode viite : suoritustapa.get("tutkinnonOsaViitteet")) {
                    if (tosaId.equals(viite.get("_tutkinnonOsa").asLong())) {
                        return viite;
                    }
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        throw new BusinessRuleViolationException("tutkinnon-osa-viite-ei-loytynyt");
    }

    @Override
    public JsonNode getTutkinnonOsaViitteet(Long id, String tyyppi) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(id);
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            if (node.get("suoritustavat") != null) {
                for (JsonNode suoritustapa : node.get("suoritustavat")) {
                    if (suoritustapa.get("suoritustapakoodi").asText().equals(tyyppi)) {
                        return suoritustapa.get("tutkinnonOsaViitteet");
                    }
                }
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        throw new BusinessRuleViolationException("tutkinnon-osa-viite-ei-loytynyt");
    }

    @Override
    public <T> T getPerusteSisalto(Long cperusteId, Class<T> type) {
        CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(cperusteId);
        return getPerusteSisalto(cperuste, type);
    }

    @Override
    public <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type) {
        try {
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            T peruste = mapper.treeToValue(node, type);
            return peruste;
        } catch (IOException ex) {
            logger.error("Perusteen parsinta epäonnistui", ex);
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet() {
        return findPerusteet(PerusteDto.class);
    }

    @Override
    public <T> List<T> findPerusteet(Class<T> type) {
        HashSet<KoulutusTyyppi> koulutustyypit = new HashSet<>();
        koulutustyypit.add(KoulutusTyyppi.AMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.ERIKOISAMMATTITUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.PERUSTUTKINTO);
        koulutustyypit.add(KoulutusTyyppi.VALMA);
        koulutustyypit.add(KoulutusTyyppi.TELMA);
        return dtoMapper.mapAsList(eperusteetClient.findPerusteet(koulutustyypit), type);
    }

    @Override
    public <T> List<T> findPerusteet(Set<String> koulutustyypit, Class<T> type) {
        if (CollectionUtils.isNotEmpty(koulutustyypit)) {
            return dtoMapper.mapAsList(eperusteetClient.findPerusteet(koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet())), type);
        } else {
            return findPerusteet(type);
        }
    }

    @Override
    public <T> T getPerusteSisaltoByPerusteId(Long perusteId, Class<T> type) {
        try {
            String perusteData = eperusteetClient.getPerusteData(perusteId);
            JsonNode node = mapper.readTree(perusteData);
            return mapper.treeToValue(node, type);
        } catch (IOException ex) {
            logger.error("Perusteen parsinta epäonnistui", ex);
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }
    }

    @Override
    public byte[] getLiite(Long perusteId, UUID id) {
        return client.exchange(eperusteetServiceUrl + "/api/perusteet/{perusteId}/kuvat/{id}", HttpMethod.GET, httpEntity, byte[].class, perusteId, id).getBody();
    }

    @Override
    public PerusteenOsaDto getPerusteenOsa(Long perusteId, Long perusteenOsaId) {
        try {
            CachedPeruste cperuste = getMostRecentCachedPerusteByPerusteId(perusteId);
            JsonNode node = mapper.readTree(cperuste.getPeruste());
            PerusteKaikkiDto peruste = mapper.treeToValue(node, PerusteKaikkiDto.class);

            if (peruste.getSisalto() != null) {
                return CollectionUtil.treeToStream(peruste.getSisalto(), PerusteenOsaViiteDto.Laaja::getLapset)
                        .filter(viite -> viite.getPerusteenOsa() != null && viite.getPerusteenOsa().getId().equals(perusteenOsaId))
                        .map(PerusteenOsaViiteDto.Laaja::getPerusteenOsa)
                        .findFirst()
                        .orElseThrow(() -> new BusinessRuleViolationException("perusteen-osaa-ei-loydy"));
            }
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
        }

        return null;
    }

    private CachedPeruste getMostRecentCachedPerusteByPerusteId(Long perusteCacheId) {
        CachedPeruste cperuste = cachedPerusteRepository.findOne(perusteCacheId);
        return cachedPerusteRepository.findFirstByPerusteIdOrderByLuotuDesc(cperuste.getPerusteId());
    }
}
