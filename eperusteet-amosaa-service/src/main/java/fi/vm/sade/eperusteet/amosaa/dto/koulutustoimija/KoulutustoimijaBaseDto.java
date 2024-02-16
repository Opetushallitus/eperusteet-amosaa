package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaBaseDto {
    Long id;
    String organisaatio;
    LokalisoituTekstiDto nimi;
    private boolean organisaatioRyhma;
}
