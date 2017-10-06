package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;

import java.util.List;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by richard.vancamp on 29/03/16.
 */
public interface TiedoteService {

    @PreAuthorize("permitAll()")
    List<TiedoteDto> getJulkisetTiedotteet(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<TiedoteDto> getTiedotteet(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    TiedoteDto getTiedote(@P("ktId") Long ktId, Long id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TiedoteDto addTiedote(@P("ktId") Long ktId, TiedoteDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TiedoteDto updateTiedote(@P("ktId") Long ktId, TiedoteDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    void deleteTiedote(@P("ktId") Long ktId, @P("id") Long id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    void kuittaaLuetuksi(@P("ktId") Long ktId, @P("id") Long id);
}
