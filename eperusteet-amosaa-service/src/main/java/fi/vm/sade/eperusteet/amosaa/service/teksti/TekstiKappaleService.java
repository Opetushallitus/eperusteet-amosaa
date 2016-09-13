/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.service.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface TekstiKappaleService {
    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto add(SisaltoViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto update(TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    TekstiKappaleDto mergeNew(SisaltoViite viite, TekstiKappaleDto tekstiKappaleDto);

    @PreAuthorize("isAuthenticated()")
    void delete(Long id);
}
