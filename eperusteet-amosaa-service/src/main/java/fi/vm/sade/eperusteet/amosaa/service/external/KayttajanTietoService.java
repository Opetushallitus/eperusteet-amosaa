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

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 *
 * @author mikkom
 */
@Service
@PreAuthorize("isAuthenticated()")
public interface KayttajanTietoService {

    // FIXME korjaa autentikaatiotasot

    KayttajanTietoDto haeKirjautaunutKayttaja();

    KayttajanTietoDto hae(String oid);

    Future<KayttajanTietoDto> haeAsync(String oid);

    List<KoulutustoimijaBaseDto> koulutustoimijat();

    Set<String> getUserOrganizations();
}
