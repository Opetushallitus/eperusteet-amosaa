package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuorituspolkuExportDto extends SuorituspolkuBaseDto {
    private BigDecimal osasuorituspolkuLaajuus;
    private Boolean piilotaPerusteenTeksti;
    private Set<SuorituspolkuRiviDto> rivit = new HashSet<>();
}
