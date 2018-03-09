package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteQueryDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.generic.rest.CachingRestClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile("default")
@Transactional
public class EperusteetClientImpl implements EperusteetClient {
    private static final Logger logger = LoggerFactory.getLogger(EperusteetClientImpl.class);

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    RestClientFactory restClientFactory;

    private CachingRestClient client;

    private ObjectMapper mapper;

    @PostConstruct
    protected void init() {
        client = restClientFactory.get(eperusteetServiceUrl, false);
        mapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    private <T> T commonGet(String endpoint, Class<T> type) {
        try {
            String url = eperusteetServiceUrl + endpoint;
            InputStream stream = client.get(url);
            T node = mapper.readValue(stream, type);
            return node;
        }
        catch (IOException ex) {
            throw new BusinessRuleViolationException("haku-epaonnistui");
        }
    }

    @Override
    public JsonNode findFromPerusteet(Map<String, String> queryDto) {
        try {
            queryDto.put("tuleva", "true");
            queryDto.put("siirtyma", "false");
            queryDto.put("voimassaolo", "true");
            queryDto.put("poistunut", "false");
            String ktparams = Arrays.stream(KoulutusTyyppi.values())
                    .map(kt -> "koulutustyyppi=" + kt.toString())
                    .collect(Collectors.joining("&"));

            String paramstr = URLEncodedUtils.format(queryDto.entrySet().stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()), Charset.defaultCharset());
            String url = eperusteetServiceUrl + "/api/perusteet" + "?" + ktparams + "&" + paramstr;
            InputStream stream = client.get(url);
            JsonNode node = mapper.readTree(stream);
            return node;
        }
        catch (IOException ex) {
            throw new BusinessRuleViolationException("haku-epaonnistui");
        }
    }

    @Override
    public String getPerusteData(Long id) {
        try {
            JsonNode node = commonGet("/api/perusteet/" + String.valueOf(id) + "/kaikki", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public <T> T getPeruste(Long id, Class<T> type) {
        T peruste = commonGet("/api/perusteet/" + id.toString() + "", type);
        return peruste;
    }

    @Override
    public <T> T getPerusteOrNull(Long id, Class<T> type) {
        try {
            T peruste = commonGet("/api/perusteet/" + id.toString() + "", type);
            return peruste;
        }
        catch (BusinessRuleViolationException ex) {
            return null;
        }
    }

    @Override
    public <T> T getPeruste(String diaarinumero, Class<T> type) {
        T peruste = commonGet("/api/perusteet/diaari?diaarinumero=" + diaarinumero, type);
        return peruste;
    }

    @Override
    public PerusteDto getYleinenPohja() {
        PerusteDto peruste = commonGet("/api/perusteet/amosaapohja", PerusteDto.class);
        return peruste;
    }

    @Override
    public String getYleinenPohjaSisalto() {
        try {
            JsonNode node = commonGet("/api/perusteet/amosaapohja", JsonNode.class);
            Object perusteObj = mapper.treeToValue(node, Object.class);
            String json = mapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        List<PerusteDto> perusteet = new ArrayList<>();
        JsonNode node = commonGet("/api/perusteet/amosaaops", JsonNode.class);
        for (JsonNode perusteJson : node) {
            try {
                PerusteDto peruste = mapper.treeToValue(perusteJson, PerusteDto.class);
                perusteet.add(peruste);
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }

        return perusteet.stream()
                .filter(peruste -> tyypit.contains(peruste.getKoulutustyyppi()))
                .collect(Collectors.toList());
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        JsonNode tiedotteet = commonGet("/api/tiedotteet" + params, JsonNode.class);
        return tiedotteet;
    }

    @Override
    public ArviointiasteikkoDto getArviointiasteikko(Long id) {
        return commonGet("/api/arviointiasteikot/" + id, ArviointiasteikkoDto.class);
    }
}