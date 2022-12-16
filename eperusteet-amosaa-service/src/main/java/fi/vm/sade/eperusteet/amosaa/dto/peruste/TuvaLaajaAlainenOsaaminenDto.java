package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("laajaalainenosaaminen")
public class TuvaLaajaAlainenOsaaminenDto extends PerusteenOsaDto.Laaja {

    private KoodiDto nimiKoodi;
    private LokalisoituTekstiDto teksti;
    private Boolean liite;

    public String getOsanTyyppi() {
        return "laajaalainenosaaminen";
    }

    public String getNimiKoodi() {
        if (nimiKoodi != null) {
            return nimiKoodi.getUri();
        }

        return null;
    }

}
