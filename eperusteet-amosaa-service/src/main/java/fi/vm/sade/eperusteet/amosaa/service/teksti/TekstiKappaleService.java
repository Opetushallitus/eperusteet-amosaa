package fi.vm.sade.eperusteet.amosaa.service.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TekstiKappaleService {

    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto add(Long opsId, SisaltoViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto update(Long opsId, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto mergeNew(SisaltoViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    void delete(Long id);
}
