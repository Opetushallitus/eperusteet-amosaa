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
public class OpintokokonaisuusTavoiteDto {
    private Long id;
    private Boolean perusteesta;
    private String tavoiteKoodi;
    private LokalisoituTekstiDto tavoite;
}
