package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViiteExportOpintokokonaisuusDto extends SisaltoViiteExportBaseDto {
    private OpintokokonaisuusDto opintokokonaisuus;
}
