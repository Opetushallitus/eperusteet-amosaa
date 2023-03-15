package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface OsaAlueService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<OletusToteutusDto> osaAlueidenOletusToteutukset(Long ktId, Long opsId);
}
