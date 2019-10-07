package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKyselyDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
@Transactional
@Profile(value = "default")
public class KayttooikeusServiceImpl implements KayttooikeusService{

    private final String VIRKAILIJA_HAKU_PATH ="/virkailija/haku";
    private final String KAYTTOOIKEUS_EPERUSTEET_AMOSAA = "EPERUSTEET_AMOSAA";
    private final List<String> ROLES = Arrays.asList("ADMIN","READ","CRUD","READ_UPDATE");

    @Value("${cas.service.kayttooikeus-service}")
    private String kayttooikeusServiceUrl;
    
    @Autowired
    private RestClientFactory restClientFactory;

    private ObjectMapper omapper = new ObjectMapper();

    @Override
    public List<KayttooikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid) {

        OphHttpClient client = restClientFactory.get(kayttooikeusServiceUrl, true);
        
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(kayttooikeusServiceUrl)
                .path(VIRKAILIJA_HAKU_PATH)
                .buildAndExpand(organisaatioOid);

        KayttooikeusKyselyDto kayttooikeusKyselyDto = KayttooikeusKyselyDto.builder()
                .kayttooikeudet(ImmutableMap.of(KAYTTOOIKEUS_EPERUSTEET_AMOSAA, ROLES))
                .organisaatioOids(Collections.singletonList(organisaatioOid))
                .build();

        OphHttpRequest request = null;
        try {
            request = OphHttpRequest.Builder
                    .post(uri.toString())
                    .setEntity(new OphHttpEntity.Builder()
                            .content(omapper.writeValueAsString(kayttooikeusKyselyDto))
                            .contentType(ContentType.APPLICATION_JSON)
                            .build())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BusinessRuleViolationException("Käyttäjien tietojen hakeminen epäonnistui");
        }

        return client.<List<KayttooikeusKayttajaDto>>execute(request)
                .handleErrorStatus(SC_UNAUTHORIZED)
                .with(res -> Optional.empty())
                .expectedStatus(SC_OK)
                .mapWith(res -> {
                    try {
                        return omapper.readValue(res, new TypeReference<List<KayttooikeusKayttajaDto>>() {});
                    } catch (IOException ex) {
                        throw new BusinessRuleViolationException("Käyttäjien tietojen hakeminen epäonnistui");
                    }
                }).orElse(new ArrayList<>());
    }
}
