package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaJulkinenDto extends KoulutustoimijaBaseDto {
    LokalisoituTekstiDto kuvaus;
    private boolean salliystavat;
}
