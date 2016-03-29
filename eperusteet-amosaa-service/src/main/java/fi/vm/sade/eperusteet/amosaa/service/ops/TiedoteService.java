package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Created by richard.vancamp on 29/03/16.
 */
@PreAuthorize("isAuthenticated()")
public interface TiedoteService {

    List<TiedoteDto> getAll(@P("ktId") Long ktId);

    TiedoteDto addTiedote(@P("ktId") Long ktId, TiedoteDto dto);

    TiedoteDto updateTiedote(@P("ktId") Long ktId, TiedoteDto dto);

    void deleteTiedote(@P("ktId") Long ktId, @P("id") Long id);
}