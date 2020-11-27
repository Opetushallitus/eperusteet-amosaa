package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface JulkaisuService {
    @PreAuthorize("permitAll()")
    List<JulkaisuBaseDto> getJulkaisut(long ktId, long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    JulkaisuBaseDto teeJulkaisu(@P("ktId") long ktId, @P("opsId") long opsId, JulkaisuBaseDto julkaisuBaseDto);
}
