package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.geneerinenarviointiasteikko.GeneerinenArviointiasteikkoKaikkiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmaTutkinnonosaExportDto extends OmaTutkinnonosaDto {
    private GeneerinenArviointiasteikkoKaikkiDto geneerinenArviointiasteikko;
}
