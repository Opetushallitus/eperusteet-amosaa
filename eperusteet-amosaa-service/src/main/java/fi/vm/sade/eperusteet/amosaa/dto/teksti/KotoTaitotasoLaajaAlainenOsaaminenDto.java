package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KotoTaitotasoLaajaAlainenOsaaminenDto {

    private Long id;
    private String koodiUri;
    private LokalisoituTekstiDto teksti;
}
