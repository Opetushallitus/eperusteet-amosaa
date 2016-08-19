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

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 *
 * @author nkala
 */
// TODO: Vaihda koulutustoimijakohtaiseen autentikaatioon
public interface KoulutustoimijaService {

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> getKoulutustoimijat(Set<String> ktOid);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> initKoulutustoimijat(Set<String> kOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KoulutustoimijaDto getKoulutustoimija(@P("ktId") Long ktId);

    @PreAuthorize("permitAll()")
    KoulutustoimijaDto getKoulutustoimijaJulkinen(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> List<T> getPaikallisetTutkinnonOsat(Long ktId, Class<T> tyyppi);
}
