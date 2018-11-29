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
package fi.vm.sade.eperusteet.amosaa.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.amosaa.dto.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author mikkom
 */
@Service
@Profile("test")
public class OrganisaatioServiceMock implements OrganisaatioService {
    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        result.putObject("nimi")
            .put("fi", "foo")
            .put("sv", "bar");
        return result;
    }

    private OrganisaatioHierarkiaDto createNode(String organisaatioOid, OrganisaatioHierarkiaDto parent) {
        OrganisaatioHierarkiaDto result = new OrganisaatioHierarkiaDto();
        result.setOid(organisaatioOid);
        result.setChildren(new ArrayList<>());
        if (parent != null) {
            result.setParentOid(parent.getOid());
            parent.getChildren().add(result);
        }
        return result;
    }

    @Override
    public OrganisaatioHierarkiaDto getOrganisaatioPuu(String organisaatioOid) {
        OrganisaatioHierarkiaDto result = createNode("1.2.246.562.10.54645809036", null);
        OrganisaatioHierarkiaDto mid = createNode("1.2.246.562.10.2013120512391252668625", result);
        createNode("1.2.246.562.10.2013120513110198396408", mid);
        result.getChildren().add(mid);
        result.setParentOid("1.2.246.562.10.54645809036");
        result.getChildren().stream()
                .forEach(midoh -> {
                    midoh.setParentOidPath(result.getOid() + "/" + midoh.getOid());
                    midoh.getChildren().stream()
                            .forEach(leaf -> {
                                leaf.setParentOidPath(result.getOid() + "/" + midoh.getOid() + "/" + leaf.getOid());
                            });
                });
        return result;
    }

}
