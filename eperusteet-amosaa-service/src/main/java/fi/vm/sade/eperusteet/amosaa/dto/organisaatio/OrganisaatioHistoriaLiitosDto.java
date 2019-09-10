package fi.vm.sade.eperusteet.amosaa.dto.organisaatio;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioHistoriaLiitosDto {

    private OrganisaatioHierarkiaDto organisaatio;
    private OrganisaatioHierarkiaDto kohde;
    private Date alkuPvm;
}
