package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuorituspolkuRiviJulkinenDto {
    private Long id;
    private LokalisoituTekstiDto kuvaus;
    private Set<String> koodit;
}
