package fi.vm.sade.eperusteet.amosaa.dto.peruste.geneerinenarviointiasteikko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArviointiAsteikkoDto {
    private Long id;
    private List<OsaamistasoDto> osaamistasot;
}
