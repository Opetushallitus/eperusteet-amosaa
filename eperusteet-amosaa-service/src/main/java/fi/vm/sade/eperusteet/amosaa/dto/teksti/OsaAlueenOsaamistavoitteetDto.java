package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.YtoOsaamistavoite;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsaAlueenOsaamistavoitteetDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private List<YtoOsaamistavoiteDto> osaamistavoitteet = new ArrayList<>();
}
