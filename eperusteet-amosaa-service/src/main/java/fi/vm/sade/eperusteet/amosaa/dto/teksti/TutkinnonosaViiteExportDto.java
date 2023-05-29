package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TutkinnonosaViiteExportDto extends SisaltoViiteExportBaseDto {
    private TutkinnonosaDto tosa;

    @JsonUnwrapped
    private TekstiKappaleJulkinenDto tekstiKappale;
}
