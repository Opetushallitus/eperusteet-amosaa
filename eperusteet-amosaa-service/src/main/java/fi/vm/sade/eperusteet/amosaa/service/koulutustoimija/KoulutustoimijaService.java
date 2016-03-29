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

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author nkala
 */
// TODO: Vaihda koulutustoimijakohtaiseen autentikaatioon
@PreAuthorize("isAuthenticated()")
public interface KoulutustoimijaService {

    KoulutustoimijaBaseDto getKoulutustoimija(String ktOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KoulutustoimijaDto getKoulutustoimija(@P("ktId") Long ktId);

    List<TiedoteDto> getTiedotteet(@P("ktId") Long ktId);

    TiedoteDto getTiedote(@P("ktId") Long ktId);

    List<TiedoteDto> getOmatTiedotteet(@P("ktId") Long ktId);
}
