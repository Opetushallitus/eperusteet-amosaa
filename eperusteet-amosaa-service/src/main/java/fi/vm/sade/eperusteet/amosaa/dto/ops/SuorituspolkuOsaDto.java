package fi.vm.sade.eperusteet.amosaa.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneOsaDto;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class SuorituspolkuOsaDto extends RakenneOsaDto {
    SuorituspolkuRiviJulkinenDto paikallinenKuvaus;
}
