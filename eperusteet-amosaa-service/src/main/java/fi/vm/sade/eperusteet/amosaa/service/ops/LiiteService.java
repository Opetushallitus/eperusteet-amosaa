package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LiiteService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    UUID add(Long ktId, @P("opsId") Long opsId, String tyyppi, String nimi, long length, InputStream is);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS') or isAuthenticated()")
    LiiteDto get(@P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    List<LiiteDto> getAll(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    void delete(@P("opsId") Long ktId, @P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS') or isAuthenticated()")
    void export(@P("opsId") Long opsId, UUID id, OutputStream os);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS')  or isAuthenticated()")
    InputStream exportLiitePerusteelta(Long opsId, UUID id);
}
