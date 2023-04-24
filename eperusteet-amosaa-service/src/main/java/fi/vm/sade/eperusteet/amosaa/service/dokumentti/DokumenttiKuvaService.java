package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiKuvaDto;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DokumenttiKuvaService {
    DokumenttiKuvaDto getDto(Long opsId, Kieli kieli);

    DokumenttiKuvaDto addImage(Long opsId, String tyyppi, Kieli kieli, MultipartFile file) throws IOException;

    DokumenttiKuva createDtoFor(Long id, Kieli kieli);

    byte[] getImage(Long opsId, String tyyppi, Kieli kieli);

    void deleteImage(Long opsId, String tyyppi, Kieli kieli);
}
