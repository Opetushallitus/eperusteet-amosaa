package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YtoOsaamistavoiteDto {
    private Long id;
    private LokalisoituTekstiDto selite;
}
