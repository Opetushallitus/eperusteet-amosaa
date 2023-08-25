package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.ExternalPdfService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "docker"})
public class ExternalPdfServiceMock implements ExternalPdfService {
    @Override
    public void generatePdf(DokumenttiDto dto, Long ktId) {
    }
}
