package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;

public interface ExternalPdfService {
    void generatePdf(DokumenttiDto dto, Long ktId, OpetussuunnitelmaKaikkiDto opsDto) throws JsonProcessingException;
    void generatePdf(DokumenttiDto dto, Long ktId) throws JsonProcessingException;
}