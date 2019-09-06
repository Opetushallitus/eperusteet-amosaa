package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttoikeusKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;
import fi.vm.sade.eperusteet.amosaa.service.util.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;

@Service
@Transactional
@Profile(value = "default")
public class KayttooikeusServiceImpl implements KayttooikeusService{

    private final String VIRKAILIJA_HAKU_PATH ="/virkailija/haku";
    
    @Value("${cas.service.kayttooikeus-service}")
    private String kayttooikeusServiceUrl;
    
    @Autowired
    private RestClientFactory restClientFactory;

    private ObjectMapper omapper = new ObjectMapper();

    @Override
    public List<KayttoikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid) {

        OphHttpClient client = restClientFactory.get(kayttooikeusServiceUrl, true);
        
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(kayttooikeusServiceUrl)
                .path(VIRKAILIJA_HAKU_PATH)
                .buildAndExpand(organisaatioOid);

        OphHttpRequest request = OphHttpRequest.Builder
                .post(uri.toString())
                .setEntity(new OphHttpEntity.Builder()
                        .content("{ \"organisaatioOids\": [\"" + organisaatioOid + "\"] }")
                        .contentType(ContentType.APPLICATION_JSON)
                        .build())
                .build();

        return client.<List<KayttoikeusKayttajaDto>>execute(request)
                .handleErrorStatus(SC_UNAUTHORIZED)
                .with(res -> Optional.empty())
                .expectedStatus(SC_OK)
                .mapWith(res -> {
                    try {                      
                        return omapper.readValue(res, new TypeReference<List<KayttoikeusKayttajaDto>>() {});
                    } catch (IOException ex) {
                        throw new BusinessRuleViolationException("Käyttäjien tietojen hakeminen epäonnistui");
                    }
                }).orElse(new ArrayList<>());
    }
}
