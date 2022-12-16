package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("opintokokonaisuus")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpintokokonaisuusDto extends PerusteenOsaDto.Laaja {

    private KoodiDto nimiKoodi;
    private Integer minimilaajuus;
    private LokalisoituTekstiDto kuvaus;
    private LokalisoituTekstiDto opetuksenTavoiteOtsikko;
    private List<KoodiDto> opetuksenTavoitteet = new ArrayList<>();
    private List<LokalisoituTekstiDto> arvioinnit;
    private LaajuusYksikko laajuusYksikko;

    public String getOsanTyyppi() {
        return "opintokokonaisuus";
    }
}
