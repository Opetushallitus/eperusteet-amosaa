package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;

public interface ExternalPdfService {
    void generatePdf(DokumenttiDto dto, Long ktId) throws JsonProcessingException;
}