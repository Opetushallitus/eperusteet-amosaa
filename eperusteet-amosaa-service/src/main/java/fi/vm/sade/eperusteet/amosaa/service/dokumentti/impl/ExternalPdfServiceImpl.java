package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalPdfServiceImpl implements ExternalPdfService {

    @Value("${fi.vm.sade.eperusteet.amosaa.pdf-service:''}")
    private String pdfServiceUrl;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private final ObjectMapper mapper = InitJacksonConverter.createMapper();

    @Override
    public void generatePdf(DokumenttiDto dto, Long ktId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        OpetussuunnitelmaKaikkiDto ops = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ktId, dto.getOpsId());
        String json = mapper.writeValueAsString(ops);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        String url = pdfServiceUrl + "/api/pdf/generate/amosaa/" + dto.getId() + "/" + dto.getKieli().name() + "/" + ktId;
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}