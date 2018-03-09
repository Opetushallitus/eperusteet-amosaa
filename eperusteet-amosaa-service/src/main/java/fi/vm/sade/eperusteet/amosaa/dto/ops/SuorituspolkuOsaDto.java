package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneOsaDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuorituspolkuOsaDto extends RakenneOsaDto {
    SuorituspolkuRiviJulkinenDto paikallinenKuvaus;
}
