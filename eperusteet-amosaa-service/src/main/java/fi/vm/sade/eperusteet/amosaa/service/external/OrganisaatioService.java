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

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface OrganisaatioService {

    @PreAuthorize("isAuthenticated()")
    JsonNode getOrganisaatio(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    OrganisaatioHierarkiaDto getOrganisaatioPuu(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    List<OrganisaatioHistoriaLiitosDto> getOrganisaationHistoriaLiitokset(String organisaatioOid);

    @PreAuthorize("permitAll()")
    LokalisoituTeksti haeOrganisaatioNimi(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaDto> getKoulutustoimijaOrganisaatiot();
}
