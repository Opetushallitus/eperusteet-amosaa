package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
public class ExternalPdfServiceImpl implements ExternalPdfService {

    @Value("${fi.vm.sade.eperusteet.amosaa.pdf-service:''}")
    private String pdfServiceUrl;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private RestClientFactory restClientFactory;

    @Lazy
    @Autowired
    private DokumenttiService dokumenttiService;

    private final ObjectMapper mapper = InitJacksonConverter.createMapper();

    @Override
    public void generatePdf(DokumenttiDto dto, Long ktId, OpetussuunnitelmaKaikkiDto opsDto) throws JsonProcessingException {
        if (opsDto == null) {
            opsDto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(ktId, dto.getOpsId());
        }

        String json = mapper.writeValueAsString(opsDto);
        OphHttpClient client = restClientFactory.get(pdfServiceUrl, true);
        String url = pdfServiceUrl + "/api/pdf/generate/amosaa/" + dto.getId() + "/" + dto.getKieli().name() + "/" + ktId;

        String result = (String) client.execute(
                        OphHttpRequest.Builder
                                .post(url)
                                .addHeader("Content-Type", "application/json;charset=UTF-8")
                                .setEntity(new OphHttpEntity.Builder()
                                        .content(json)
                                        .contentType(ContentType.APPLICATION_JSON)
                                        .build())
                                .build())
                .handleErrorStatus(SC_FOUND, SC_UNAUTHORIZED, SC_FORBIDDEN, SC_METHOD_NOT_ALLOWED, SC_BAD_REQUEST, SC_INTERNAL_SERVER_ERROR, SC_NOT_FOUND)
                .with(res -> Optional.of("error"))
                .expectedStatus(SC_ACCEPTED)
                .mapWith(res -> res)
                .orElse(null);

        if (!ObjectUtils.isEmpty(result)) {
            throw new RuntimeException("Virhe pdf-palvelun kutsussa");
        }

    }

    @Override
    public void generatePdf(DokumenttiDto dto, Long ktId) throws JsonProcessingException {
        generatePdf(dto, ktId, null);
    }
}
