package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import java.io.IOException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("test")
public class DokumenttiServiceMock implements DokumenttiService {
    @Override
    public DokumenttiDto getDto(Long ktId, Long opsId, Kieli kieli) {
        DokumenttiDto dto = new DokumenttiDto();
        dto.setId(new Double(Math.random() * 1000).longValue());
        return dto;
    }

    @Override
    public DokumenttiDto update(Long ktId, Long opsId, Kieli kieli, DokumenttiDto dto) {
        return null;
    }

    @Override
    public DokumenttiDto createDtoFor(Long ktId, Long opsId, Kieli kieli) {
        return null;
    }

    @Override
    public void setStarted(Long ktId, Long opsId, DokumenttiDto dto) {

    }

    @Override
    public void generateWithDto(Long ktId, Long opsId, DokumenttiDto dto) throws DokumenttiException {

    }

    @Override
    public DokumenttiDto addImage(Long ktId, Long opsId, DokumenttiDto dto, String tyyppi, String kieli, MultipartFile image) throws IOException {
        return null;
    }

    @Override
    public byte[] get(Long ktId, Long opsId, Kieli kieli) {
        return new byte[0];
    }

    @Override
    public void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId) {

    }

    @Override
    public void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId) {

    }
}
