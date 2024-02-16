package fi.vm.sade.eperusteet.amosaa.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioStatus;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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

    @Override
    public List<OrganisaatioHistoriaLiitosDto> getOrganisaationHistoriaLiitokset(String organisaatioOid) {

        return Arrays.asList(
                OrganisaatioHistoriaLiitosDto.builder()
                        .organisaatio(OrganisaatioHierarkiaDto.builder()
                                .oid("1.2.246.562.10.54645809036")
                                .status(OrganisaatioStatus.PASSIIVINEN)
                                .build())
                        .build(),
                OrganisaatioHistoriaLiitosDto.builder()
                        .organisaatio(OrganisaatioHierarkiaDto.builder()
                                .oid("1.2.246.562.10.2013120512391252668625")
                                .status(OrganisaatioStatus.AKTIIVINEN)
                                .build())
                        .build()
        );
    }

    @Override
    public LokalisoituTeksti haeOrganisaatioNimi(String organisaatioOid) {
        return LokalisoituTeksti.of(ImmutableMap.of(Kieli.FI, "faa", Kieli.SV, "bor"));
    }

    @Override
    public List<KoulutustoimijaDto> getKoulutustoimijaOrganisaatiot() {
        return null;
    }

}
