package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.*;
import lombok.*;

@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TutkinnonosaExportDto extends SisaltoViiteExportBaseDto {
    private TutkinnonosaDto tosa;

    @JsonUnwrapped
    private TekstiKappaleJulkinenDto tekstiKappale;
}
