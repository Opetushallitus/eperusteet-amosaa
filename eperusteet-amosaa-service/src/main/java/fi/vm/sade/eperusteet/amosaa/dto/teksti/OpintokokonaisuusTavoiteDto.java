package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class OpintokokonaisuusTavoiteDto {
    private Long id;
    private Boolean perusteesta;
    private String tavoiteKoodi;
    private LokalisoituTekstiDto tavoite;
}
