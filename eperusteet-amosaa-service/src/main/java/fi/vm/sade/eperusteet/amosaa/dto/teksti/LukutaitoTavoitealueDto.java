package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LukutaitoTavoitealueDto {
    private Long id;
    private LokalisoituTekstiDto paikallinenTarkennus;
}
