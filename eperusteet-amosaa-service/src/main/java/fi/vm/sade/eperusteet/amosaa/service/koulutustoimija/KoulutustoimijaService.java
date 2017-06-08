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
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

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
    KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(@P("ktId") Long ktId);

    @PreAuthorize("permitAll()")
    KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(@P("ktId") String ktOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> List<T> getPaikallisetTutkinnonOsat(@P("ktId") Long ktId, Class<T> tyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    KoulutustoimijaDto updateKoulutustoimija(@P("ktId") Long ktId, KoulutustoimijaDto kt);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KoulutustoimijaYstavaDto> getOmatYstavat(Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KoulutustoimijaBaseDto> getPyynnot(Long ktId);

    Long getKoulutustoimija(String idTaiOid);

    @PreAuthorize("permitAll()")
    Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(PageRequest page, KoulutustoimijaQueryDto query);
}
