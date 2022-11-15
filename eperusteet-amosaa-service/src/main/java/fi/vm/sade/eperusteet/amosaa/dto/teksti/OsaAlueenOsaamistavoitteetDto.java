package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.YtoOsaamistavoite;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OsaAlueenOsaamistavoitteetDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private List<YtoOsaamistavoiteDto> osaamistavoitteet = new ArrayList<>();
}
