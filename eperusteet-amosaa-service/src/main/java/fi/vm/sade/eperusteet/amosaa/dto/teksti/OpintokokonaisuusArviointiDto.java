package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class OpintokokonaisuusArviointiDto {

    private Long id;
    private Boolean perusteesta;
    private LokalisoituTekstiDto arviointi;

}
