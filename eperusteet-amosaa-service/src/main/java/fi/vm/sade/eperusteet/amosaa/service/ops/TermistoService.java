package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TermistoService {

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS') or isAuthenticated()")
    List<TermiDto> getTermit(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS') or isAuthenticated()")
    TermiDto getTermi(@P("ktId") Long ktId, Long id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS') or isAuthenticated()")
    TermiDto getTermiByAvain(@P("ktId") Long ktId, String avain);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TermiDto addTermi(@P("ktId") Long ktId, TermiDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TermiDto updateTermi(@P("ktId") Long ktId, TermiDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    void deleteTermi(@P("ktId") Long ktId, @P("id") Long id);
}
