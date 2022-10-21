package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface JulkaisuService {
    @PreAuthorize("permitAll()")
    List<JulkaisuBaseDto> getJulkaisut(long ktId, long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    JulkaisuBaseDto teeJulkaisu(@P("ktId") long ktId, @P("opsId") long opsId, JulkaisuBaseDto julkaisuBaseDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    JulkaisuBaseDto aktivoiJulkaisu(@P("ktId") long ktId, @P("opsId") long opsId, int revision);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'oph', 'HALLINTA')")
    void kooditaSisaltoviitteet(@P("ktId") long ktId, @P("opsId") long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    boolean onkoMuutoksia(long ktId, long opsId);
}
