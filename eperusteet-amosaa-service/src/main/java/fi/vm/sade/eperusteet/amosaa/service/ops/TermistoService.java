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
package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author apvilkko
 */

public interface TermistoService {

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS')")
    List<TermiDto> getTermit(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS')")
    TermiDto getTermi(@P("ktId") Long ktId, Long id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'ESITYS')")
    TermiDto getTermiByAvain(@P("ktId") Long ktId, String avain);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TermiDto addTermi(@P("ktId") Long ktId, TermiDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    TermiDto updateTermi(@P("ktId") Long ktId, TermiDto dto);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    void deleteTermi(@P("ktId") Long ktId, @P("id") Long id);
}
