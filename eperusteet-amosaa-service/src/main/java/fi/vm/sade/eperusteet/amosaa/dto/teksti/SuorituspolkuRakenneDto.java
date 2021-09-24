package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuorituspolkuRakenneDto extends RakenneModuuliDto {
    private SuorituspolkuRiviJulkinenDto paikallinenKuvaus;

}
