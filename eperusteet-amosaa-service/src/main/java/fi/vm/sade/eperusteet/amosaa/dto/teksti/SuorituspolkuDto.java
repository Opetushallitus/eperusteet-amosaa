package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuorituspolkuDto extends SuorituspolkuBaseDto {
    private Boolean naytaKuvausJulkisesti;
    private Boolean piilotaPerusteenTeksti;
    private Set<SuorituspolkuRiviDto> rivit = new HashSet<>();
    private BigDecimal osasuorituspolkuLaajuus;
}
