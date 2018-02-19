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

package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author nkala
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioHierarkia {
    private Map<Kieli, String> nimi;
    private String oid;
    private String parentOid;
    private String parentOidPath;
    private String kotipaikkaUri;
    private List<String> organisaatiotyypit;
    private List<OrganisaatioHierarkia> children;

    public Stream<OrganisaatioHierarkia> getAll() {
        return Stream.concat(
                Stream.of(this),
                getChildren().stream()
                        .distinct()
                        .map(OrganisaatioHierarkia::getAll)
                        .flatMap(x -> x));
    }
}