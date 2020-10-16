package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OpintokokonaisuusDto {
    private Long id;
    private String nimiKoodi;
    private LokalisoituTekstiDto kuvaus;
    private Integer minimilaajuus;
    private LokalisoituTekstiDto opetuksenTavoiteOtsikko;
    private List<OpintokokonaisuusTavoiteDto> tavoitteet = new ArrayList<>();
    private LokalisoituTekstiDto keskeisetSisallot;
    private LokalisoituTekstiDto arvioinninKuvaus;
    private List<OpintokokonaisuusArviointiDto> arvioinnit = new ArrayList<>();
}
