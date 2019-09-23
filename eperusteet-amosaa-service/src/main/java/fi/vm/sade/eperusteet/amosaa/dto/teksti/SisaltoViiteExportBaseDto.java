package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import lombok.Getter;
import lombok.Setter;

/**
 * Käytetään kaikkien sisältösolmujen pohjana nimen ja tyyppitiedot viemiseen
 */
@Getter
@Setter
public class SisaltoViiteExportBaseDto {
    private Long id;
    private SisaltoTyyppi tyyppi;
}
