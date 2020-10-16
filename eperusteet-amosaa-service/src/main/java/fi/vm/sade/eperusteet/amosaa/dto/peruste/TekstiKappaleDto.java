package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("tekstikappale")
public class TekstiKappaleDto extends PerusteenOsaDto.Laaja {
    private LokalisoituTekstiDto teksti;

    public TekstiKappaleDto() {
    }

    public String getOsanTyyppi() {
        return "tekstikappale";
    }
}
