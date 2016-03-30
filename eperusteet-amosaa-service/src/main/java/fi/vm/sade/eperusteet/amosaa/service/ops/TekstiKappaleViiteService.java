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

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
@PreAuthorize("isAuthenticated()")
public interface TekstiKappaleViiteService extends RevisionService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleViiteDto.Matala getTekstiKappaleViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getTekstiKappaleViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> List<T> getTekstiKappaleViitteet(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId,
                                                       TekstiKappaleViiteDto.Matala viiteDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto updateTekstiKappaleViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long rootViiteId, TekstiKappaleViiteDto uusi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeTekstiKappaleViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void reorderSubTree(@P("ktId") Long ktId, @P("opsId") Long opsId, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi);

    TekstiKappaleViite kopioiHierarkia(TekstiKappaleViite original, Opetussuunnitelma owner);
}

