package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@JsonTypeName("tekstikappale")
public class TekstiKappaleDto extends PerusteenOsaDto.Laaja {
    private LokalisoituTekstiDto teksti;

    public TekstiKappaleDto() {
    }

    public String getOsanTyyppi() {
        return "tekstikappale";
    }
}
