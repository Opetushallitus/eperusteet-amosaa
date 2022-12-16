package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTyyppi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.LaajuusYksikko;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpintokokonaisuusDto {
    private Long id;
    private String nimiKoodi;
    private String koodi;
    private LokalisoituTekstiDto kuvaus;
    private BigDecimal laajuus;
    private LaajuusYksikko laajuusYksikko;
    private Integer minimilaajuus;
    private OpintokokonaisuusTyyppi tyyppi;
    private LokalisoituTekstiDto opetuksenTavoiteOtsikko;
    private List<OpintokokonaisuusTavoiteDto> tavoitteet = new ArrayList<>();
    private LokalisoituTekstiDto tavoitteidenKuvaus;
    private LokalisoituTekstiDto keskeisetSisallot;
    private LokalisoituTekstiDto arvioinninKuvaus;
    private List<OpintokokonaisuusArviointiDto> arvioinnit = new ArrayList<>();

    public String getKoodiArvo() {
        if (koodi != null) {
            return koodi.substring(koodi.indexOf("_") + 1);
        }

        return null;
    }

}
