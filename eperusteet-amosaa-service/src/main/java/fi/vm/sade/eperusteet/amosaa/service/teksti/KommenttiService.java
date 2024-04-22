package fi.vm.sade.eperusteet.amosaa.service.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;

import java.util.List;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface KommenttiService {
    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<KommenttiDto> getAllByTekstikappaleviite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long tkvId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    KommenttiDto get(@P("ktId") Long ktId, @P("opsId") Long opsId, Long kommenttiId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'KOMMENTOINTI')")
    KommenttiDto add(@P("ktId") Long ktId, @P("opsId") Long opsId, KommenttiDto kommenttiDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'KOMMENTOINTI')")
    KommenttiDto update(@P("ktId") Long ktId, @P("opsId") Long opsId, Long kommenttiId, KommenttiDto kommenttiDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'KOMMENTOINTI')")
    void delete(@P("ktId") Long ktId, @P("opsId") Long opsId, Long kommenttiId);
}
