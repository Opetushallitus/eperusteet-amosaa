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

import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author jhyoty
 */
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
