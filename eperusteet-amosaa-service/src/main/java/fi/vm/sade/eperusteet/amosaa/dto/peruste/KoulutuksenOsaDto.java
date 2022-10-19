package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("koulutuksenosa")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoulutuksenOsaDto extends PerusteenOsaDto.Laaja {

    private KoodiDto nimiKoodi;
    private Integer laajuusMinimi;
    private Integer laajuusMaksimi;
    private KoulutusOsanKoulutustyyppi koulutusOsanKoulutustyyppi;
    private KoulutusOsanTyyppi koulutusOsanTyyppi;
    private LokalisoituTekstiDto kuvaus;
    private List<LokalisoituTekstiDto> tavoitteet;
    private LokalisoituTekstiDto keskeinenSisalto;
    private LokalisoituTekstiDto laajaAlaisenOsaamisenKuvaus;
    private LokalisoituTekstiDto arvioinninKuvaus;

    public String getOsanTyyppi() {
        return "koulutuksenosa";
    }

    public String getNimiKoodi() {
        if (nimiKoodi != null) {
            return nimiKoodi.getUri();
        }

        return null;
    }

}
