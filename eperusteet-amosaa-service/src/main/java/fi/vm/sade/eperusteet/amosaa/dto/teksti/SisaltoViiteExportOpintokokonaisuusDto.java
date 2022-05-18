package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SisaltoViiteExportOpintokokonaisuusDto extends SisaltoViiteExportBaseDto {
    private OpintokokonaisuusDto opintokokonaisuus;
}
