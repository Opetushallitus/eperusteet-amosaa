package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

/**
 * Käytetään tutkinnon osien viemiseen kokonaisuudessaan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViiteExportTutkinnonosaDto extends SisaltoViiteExportBaseDto {
    private TutkinnonosaDto tosa;
}
