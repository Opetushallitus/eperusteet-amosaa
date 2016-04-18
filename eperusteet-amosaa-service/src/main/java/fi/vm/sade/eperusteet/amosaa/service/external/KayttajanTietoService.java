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
package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 *
 * @author mikkom
 */
@Service
public interface KayttajanTietoService {

    // FIXME Tarkasta halutaanko näitä tiukentaa

    @PreAuthorize("isAuthenticated()")
    String getUserOid();

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto haeKirjautaunutKayttaja();

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto hae(String oid);

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto hae(Long id);

    @PreAuthorize("isAuthenticated()")
    Kayttaja getKayttaja();

    @PreAuthorize("isAuthenticated()")
    Future<KayttajanTietoDto> haeAsync(String oid);

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto haeNimi(Long id);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> koulutustoimijat();

    @PreAuthorize("isAuthenticated()")
    boolean updateKoulutustoimijat();

    @PreAuthorize("isAuthenticated()")
    Set<String> getUserOrganizations();

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KayttajanTietoDto getKayttaja(@P("ktId") Long ktId, Long oid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KayttajaDto> getKayttajat(@P("ktId") Long ktId);
}
