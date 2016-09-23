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

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author nkala
 */
public interface OpetussuunnitelmaService extends RevisionService {

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaBaseDto> getPohjat();

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaDto> getJulkisetOpetussuunnitelmat(Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS')")
    Long getKoulutustoimijaId(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaDto getOpetussuunnitelma(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuDto> getPoistetut(Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<KayttajaoikeusDto> getOikeudet(Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'HALLINTA')")
    KayttajaoikeusDto updateOikeus(Long ktId, @P("opsId") Long opsId, Long oikeusId, KayttajaoikeusDto oikeus);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto update(Long ktId, @P("opsId") Long opsId, OpetussuunnitelmaDto body);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUONTI')")
    OpetussuunnitelmaBaseDto addOpetussuunnitelma(@P("ktId") Long ktId, OpetussuunnitelmaDto opsDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaBaseDto updateTila(Long ktId, @P("opsId") Long opsId, @P("tila") Tila tila);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Validointi validoi(@P("ktId") Long ktId, @P("opsId") Long opsId);
}

