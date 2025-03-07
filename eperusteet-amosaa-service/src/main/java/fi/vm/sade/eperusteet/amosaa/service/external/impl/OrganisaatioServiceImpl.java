package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Service
@Profile("default")
public class OrganisaatioServiceImpl implements OrganisaatioService {

    private static final String ORGANISAATIOT = "/rest/organisaatio/";

    @Autowired
    private Client client;

    private final ObjectMapper mapper = new ObjectMapper();

    @Component
    public static class Client {
        @Autowired
        private RestClientFactory restClientFactory;

        @Value("${cas.service.organisaatio-service:''}")
        private String serviceUrl;

        private final ObjectMapper mapper = new ObjectMapper();

        @Cacheable("organisaatiot")
        public JsonNode getOrganisaatio(String organisaatioOid) {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + organisaatioOid;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .orElse(null);
        }

        public JsonNode getOrganisaatioPuu(String organisaatioOid) {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + "v2/hierarkia/hae?aktiiviset=true&suunnitellut=true&lakkautetut=false&oid=" + organisaatioOid;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .orElse(null);
        }

        public JsonNode getOrganisaationHistoriaLiitokset(String organisaatioOid) {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .path(ORGANISAATIOT)
                    .path("v4/{organisaatioOid}/historia")
                    .buildAndExpand(organisaatioOid);

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(uri.toString())
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .orElse(null);
        }

        public JsonNode getKoulutustoimijat() {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + "v4/hae?aktiiviset=true&suunnitellut=true&lakkautetut=false&organisaatiotyyppi=organisaatiotyyppi_01";

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .orElse(null);
        }
    }

    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        return client.getOrganisaatio(organisaatioOid);
    }

    private void removeUnwanteds(ArrayNode parent) {
        Iterator<JsonNode> iterator = parent.iterator();
        while (iterator.hasNext()) {
            JsonNode child = iterator.next();

            if (child.hasNonNull("oppilaitostyyppi")) {
                String oppilaitostyyppi = child.get("oppilaitostyyppi").asText();
                // Poistetaan jos ei ole sallittu oppilaitostyyppi (ks. oppilaitostyyppi koodistopalvelusta)
                if (Stream.of(21, 22, 23, 24, 29, 93)
                        .map(olt -> "oppilaitostyyppi_" + olt)
                        .noneMatch(oppilaitostyyppi::startsWith)) {
                    iterator.remove();
                }
            }

            if (child.hasNonNull("children")) {
                ArrayNode children = (ArrayNode) child.get("children");
                removeUnwanteds(children);
            }
        }
    }

    @Override
    public OrganisaatioHierarkiaDto getOrganisaatioPuu(String organisaatioOid) {
        JsonNode puu = client.getOrganisaatioPuu(organisaatioOid);
        try {
            if (puu.get("organisaatiot").size() == 0 || SecurityUtil.OPH_OID.equals(organisaatioOid)) {
                return null;
            }
            JsonNode organisaatiot = puu.get("organisaatiot").get(0);

            if (organisaatiot.hasNonNull("children")) {
                removeUnwanteds((ArrayNode) organisaatiot.get("children"));
            }

            OrganisaatioHierarkiaDto hierarkia = mapper.treeToValue(organisaatiot, OrganisaatioHierarkiaDto.class);
            return hierarkia;
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public List<OrganisaatioHistoriaLiitosDto> getOrganisaationHistoriaLiitokset(String organisaatioOid) {
        JsonNode puu = client.getOrganisaationHistoriaLiitokset(organisaatioOid);
        try {
            if (puu.get("liitokset").size() == 0 || SecurityUtil.OPH_OID.equals(organisaatioOid)) {
                return null;
            }
            JsonNode organisaatiot = puu.get("liitokset");

            ObjectReader reader = mapper.readerFor(new TypeReference<List<OrganisaatioHistoriaLiitosDto>>() {});
            return reader.readValue(organisaatiot);
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    @Cacheable("organisaationimi")
    public LokalisoituTeksti haeOrganisaatioNimi(String organisaatioOid) {
        JsonNode organisaatio = getOrganisaatio(organisaatioOid);
        if (organisaatio == null) {
            return LokalisoituTeksti.of(Kieli.FI, "Tuntematon organisaatio");
        }
        return LokalisoituTeksti.of(organisaatio.get("nimi"));
    }

    @Override
    @Cacheable("koulutuksenjarjestajat")
    public List<KoulutustoimijaDto> getKoulutustoimijaOrganisaatiot() {
        JsonNode koulutustoimijatNode = client.getKoulutustoimijat().get("organisaatiot");
        List<KoulutustoimijaDto> koulutustoimijat = new ArrayList<>();

        koulutustoimijatNode.forEach(koulutustoimija -> {
            KoulutustoimijaDto koulutustoimijaDto = new KoulutustoimijaDto();
            koulutustoimijaDto.setNimi(LokalisoituTekstiDto.of(koulutustoimija.get("nimi")));
            koulutustoimijaDto.setOrganisaatio(koulutustoimija.get("oid").asText());
            koulutustoimijat.add(koulutustoimijaDto);
        });
        return koulutustoimijat;
    }
}
