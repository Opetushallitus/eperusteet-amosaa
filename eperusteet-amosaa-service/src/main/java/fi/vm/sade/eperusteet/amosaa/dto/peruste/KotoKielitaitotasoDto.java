package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonTypeName("koto_kielitaitotaso")
public class KotoKielitaitotasoDto extends PerusteenOsaDto.Laaja {

    private KoodiDto nimiKoodi;
    private LokalisoituTekstiDto kuvaus;
    private List<KotoTaitotasoDto> taitotasot = new ArrayList<>();

    @Override
    public String getOsanTyyppi() {
        return "koto_kielitaitotaso";
    }
}
