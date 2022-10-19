package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KotoTaitotasoDto {

    private Long id;
    private String koodiUri;
    private LokalisoituTekstiDto tavoiteTarkennus;
    private LokalisoituTekstiDto sisaltoTarkennus;

}
