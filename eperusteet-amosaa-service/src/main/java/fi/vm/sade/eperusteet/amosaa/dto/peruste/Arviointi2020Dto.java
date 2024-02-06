package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.utils.dto.utils.LokalisoituTekstiDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arviointi2020Dto {
    private Long id;
    private LokalisoituTekstiDto kohde;
    @JsonProperty("_arviointiAsteikko")
    private Long arviointiAsteikko;
    private List<OsaamistasonKriteerit2020Dto> osaamistasonKriteerit = new ArrayList<>();
}
