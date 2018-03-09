package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuorituspolkuRakenneDto extends RakenneModuuliDto {
    private SuorituspolkuRiviJulkinenDto paikallinenKuvaus;
}
