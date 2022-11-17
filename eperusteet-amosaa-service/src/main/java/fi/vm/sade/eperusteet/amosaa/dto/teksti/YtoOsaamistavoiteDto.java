package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YtoOsaamistavoiteDto {
    private Long id;
    private LokalisoituTekstiDto selite;
}