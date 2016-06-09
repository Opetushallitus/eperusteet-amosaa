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
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author jhyoty
 */
public interface LiiteService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    UUID add(Long ktId, @P("opsId") Long opsId, String tyyppi, String nimi, long length, InputStream is);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    LiiteDto get(@P("ktId") Long ktId, UUID id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<LiiteDto> getAll(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
    void delete(@P("opsId") Long ktId, UUID id);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    void export(@P("ktId") Long ktId, UUID id, OutputStream os);
}
