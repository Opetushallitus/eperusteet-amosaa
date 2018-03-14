package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Getter;
import lombok.Setter;

/**
 * Käytetään tutkinnon osien viemiseen kokonaisuudessaan
 */
@Getter
@Setter
public class SisaltoViiteExportTutkinnonosaDto extends SisaltoViiteExportBaseDto {
    private TutkinnonosaDto tosa;
}
