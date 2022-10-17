package fi.vm.sade.eperusteet.amosaa.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuorituspolkuRiviJulkinenDto {
    private Long id;
    private LokalisoituTekstiDto kuvaus;
    private Set<String> koodit;
}
