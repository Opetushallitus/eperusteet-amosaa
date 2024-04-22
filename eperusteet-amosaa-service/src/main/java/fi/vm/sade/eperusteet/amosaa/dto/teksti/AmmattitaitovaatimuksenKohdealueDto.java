package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class AmmattitaitovaatimuksenKohdealueDto {
    private LokalisoituTekstiDto otsikko;
    private List<AmmattitaitovaatimuksenKohdeDto> vaatimuksenKohteet = new ArrayList<>();
}
