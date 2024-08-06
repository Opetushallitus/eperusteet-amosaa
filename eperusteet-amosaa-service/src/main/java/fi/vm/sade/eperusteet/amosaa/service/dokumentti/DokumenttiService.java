package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DokumenttiService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    DokumenttiDto getValmisDto(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    DokumenttiDto update(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli, DokumenttiDto dto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'oph', 'HALLINTA')")
    DokumenttiDto createDtoFor(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    DokumenttiDto getLatestDokumentti(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    DokumenttiDto getJulkaistuDokumentti(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli, Integer revision);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'oph', 'HALLINTA')")
    void setStarted(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'oph', 'HALLINTA')")
    void generateWithDto(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto) throws DokumenttiException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'oph', 'HALLINTA')")
    void generateWithDto(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto, OpetussuunnitelmaKaikkiDto opsDto) throws DokumenttiException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    DokumenttiDto get(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    byte[] getDataByDokumenttiId(@P("ktId") Long ktId, @P("opsId") Long opsId, Long dokumenttiId);

    void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);

    void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);
}
