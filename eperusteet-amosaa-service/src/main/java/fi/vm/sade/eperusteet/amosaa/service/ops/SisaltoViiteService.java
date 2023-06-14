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

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.resource.locks.contexts.SisaltoViiteCtx;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface SisaltoViiteService extends LockService<SisaltoViiteCtx> {
    default SisaltoViiteDto.Matala getSisaltoRoot(Long ktId, Long opsId) {
        return getSisaltoRoot(ktId, opsId, SisaltoViiteDto.Matala.class);
    }

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T> T getSisaltoRoot(Long ktId, Long opsId, Class<T> t);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    SisaltoViiteRakenneDto getRakenne(Long ktId, Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    SisaltoViiteDto.Matala getSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    <T> T getSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, Class<T> t);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T> List<T> getSisaltoViitteet(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> t);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'POISTO')")
    SisaltoViiteDto restoreSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long poistettuId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto.Matala addSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId,
                                           SisaltoViiteDto.Matala viiteDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, SisaltoViiteDto uusi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'OPH','HALLINTA')")
    void removeSisaltoViite(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto.Puu kloonaaTekstiKappale(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void reorderSubTree(@P("ktId") Long ktId, @P("opsId") Long opsId, Long rootViiteId, SisaltoViiteRakenneDto uusi);

    SisaltoViite kopioiHierarkia(SisaltoViite original, Opetussuunnitelma owner, Map<SisaltoTyyppi, Set<String>> sisaltotyyppiIncludes, SisaltoViite.TekstiHierarkiaKopiointiToiminto kopiointiType);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    RevisionDto getLatestRevision(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<RevisionKayttajaDto> getRevisions(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    SisaltoViiteDto getData(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, Integer revId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> List<T> getByKoodi(@P("ktId") Long ktId, String koodi, Class<T> tyyppi);

    @PreAuthorize("permitAll()")
    <T> List<T> getByKoodiJulkinen(@P("ktId") Long ktId, String koodi, Class<T> tyyppi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void revertToVersion(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId, Integer versio);

    @PreAuthorize("isAuthenticated()")
    int getCountByKoodi(Long ktId, String koodi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    List<SisaltoViiteDto> copySisaltoViiteet(@P("ktId") Long ktId, @P("opsId") Long opsId, List<Long> viitteet);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T> List<T> getTutkinnonOsaViitteet(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> aClass);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T> List<T> getTutkinnonOsat(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> aClass);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T> List<T> getSuorituspolut(@P("ktId") Long ktId, @P("opsId") Long opsId, Class<T> aClass);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    List<SuorituspolkuRakenneDto> getSuorituspolkurakenne(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("permitAll()")
    SuorituspolkuRakenneDto luoSuorituspolkuRakenne(RakenneModuuliDto rakenne, SisaltoViiteDto suorituspolunViite);

    @PreAuthorize("isAuthenticated()")
    void updateOpetussuunnitelmaPiilotetutSisaltoviitteet(SisaltoViiteDto sisaltoviite, Opetussuunnitelma opetussuunnitelma);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> Page<T> getSisaltoviitteetWithQuery(@P("ktId") Long ktId, SisaltoviiteQueryDto query, Class<T> tyyppi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    List<SisaltoViiteDto> getSisaltoviitteet(Long ktId, Long opsId, SisaltoTyyppi tyyppi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    List<SisaltoViiteDto> getOpetussuunnitelmanPohjanSisaltoviitteet(Long ktId, Long opsId, SisaltoTyyppi tyyppi);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void linkSisaltoViiteet(@P("ktId") Long ktId, @P("opsId") Long opsId, List<Long> viitteet);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    SisaltoViiteDto kopioiLinkattuSisaltoViiteet(@P("ktId") Long ktId, @P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<OletusToteutusDto> tutkinnonosienOletusToteutukset(@P("ktId") Long ktId, @P("opsId") Long opsId);
}

