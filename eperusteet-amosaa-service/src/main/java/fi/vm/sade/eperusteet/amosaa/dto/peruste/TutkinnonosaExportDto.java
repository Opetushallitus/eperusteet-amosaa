package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutkinnonosaExportDto extends SisaltoViiteExportBaseDto {
    private TutkinnonosaDto tosa;

    @JsonUnwrapped
    private TekstiKappaleJulkinenDto tekstiKappale;
}
