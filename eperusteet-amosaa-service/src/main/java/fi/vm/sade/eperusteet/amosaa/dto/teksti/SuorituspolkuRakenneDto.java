package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SuorituspolkuRakenneDto extends RakenneModuuliDto {
    private SuorituspolkuRiviDto paikallinenKuvaus;
    private Long sisaltoviiteId;
}
