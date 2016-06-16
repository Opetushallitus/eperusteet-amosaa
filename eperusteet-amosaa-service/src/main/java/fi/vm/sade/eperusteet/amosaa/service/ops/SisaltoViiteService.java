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
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface SisaltoViiteService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    SisaltoViiteDto.Matala getSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> List<T> getSisaltoViitteet(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    SisaltoViiteDto restoreSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long poistettuId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto.Matala addSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId,
                                                       SisaltoViiteDto.Matala viiteDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto updateSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long rootViiteId, SisaltoViiteDto uusi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto.Puu kloonaaTekstiKappale(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void reorderSubTree(@P("ktId") Long ktId, @P("opsId") Long opsId, Long rootViiteId, SisaltoViiteDto.Puu uusi);

    SisaltoViite kopioiHierarkia(SisaltoViite original, Opetussuunnitelma owner);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    RevisionDto getLatestRevision(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionDto> getRevisions(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    SisaltoViiteDto getData(@P("opsId") Long opsId, Long viiteId, Integer revId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> List<T> getByKoodi(@P("ktId") Long ktId, String koodi, Class<T> tyyppi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void revertToVersion(@P("opsId") Long opsId, Long viiteId, Integer versio);
}

