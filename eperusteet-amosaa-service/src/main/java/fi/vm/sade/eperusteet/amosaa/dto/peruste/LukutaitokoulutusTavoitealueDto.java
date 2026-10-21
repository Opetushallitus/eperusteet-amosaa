package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LukutaitokoulutusTavoitealueDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private List<LokalisoituTekstiDto> tavoitteet = new ArrayList<>();
}
