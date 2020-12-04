package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTyyppi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OpintokokonaisuusDto {
    private Long id;
    private String nimiKoodi;
    private LokalisoituTekstiDto kuvaus;
    private BigDecimal laajuus;
    private Integer minimilaajuus;
    private OpintokokonaisuusTyyppi tyyppi;
    private LokalisoituTekstiDto opetuksenTavoiteOtsikko;
    private List<OpintokokonaisuusTavoiteDto> tavoitteet = new ArrayList<>();
    private LokalisoituTekstiDto tavoitteidenKuvaus;
    private LokalisoituTekstiDto keskeisetSisallot;
    private LokalisoituTekstiDto arvioinninKuvaus;
    private List<OpintokokonaisuusArviointiDto> arvioinnit = new ArrayList<>();
}
