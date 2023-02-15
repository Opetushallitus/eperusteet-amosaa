package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DokumenttiService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    DokumenttiDto getDto(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    DokumenttiDto update(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli, DokumenttiDto dto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    DokumenttiDto createDtoFor(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void setStarted(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void generateWithDto(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto) throws DokumenttiException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    DokumenttiDto addImage(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto, String tyyppi, String kieli, MultipartFile image) throws IOException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    byte[] get(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);

    void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);
}
