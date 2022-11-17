package fi.vm.sade.eperusteet.amosaa.dto.organisaatio;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioHistoriaLiitosDto {

    private OrganisaatioHierarkiaDto organisaatio;
    private OrganisaatioHierarkiaDto kohde;
    private Date alkuPvm;
}
