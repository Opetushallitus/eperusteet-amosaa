package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViiteExportOpintokokonaisuusDto extends SisaltoViiteExportBaseDto {
    private OpintokokonaisuusDto opintokokonaisuus;
}
