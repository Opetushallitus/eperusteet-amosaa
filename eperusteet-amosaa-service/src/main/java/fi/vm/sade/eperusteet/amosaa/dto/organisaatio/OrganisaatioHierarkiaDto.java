package fi.vm.sade.eperusteet.amosaa.dto.organisaatio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioHierarkiaDto {
    private Map<Kieli, String> nimi = new HashMap<>();
    private String oid;
    private String parentOid;
    private String parentOidPath;
    private String kotipaikkaUri;
    private List<String> organisaatiotyypit = new ArrayList<>();
    private List<OrganisaatioHierarkiaDto> children = new ArrayList<>();
    private OrganisaatioStatus status;

    public List<OrganisaatioHierarkiaDto> getChildren() {
        return children != null ? children : new ArrayList<>();
    }

    @JsonIgnore
    public Stream<OrganisaatioHierarkiaDto> getAll() {
        return Stream.concat(
                Stream.of(this),
                getChildren().stream()
                        .distinct()
                        .map(OrganisaatioHierarkiaDto::getAll)
                        .flatMap(x -> x));
    }
}
