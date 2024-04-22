package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KevytTekstiKappaleDto {
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
}
