package fi.vm.sade.eperusteet.amosaa.dto.peruste;

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
@JsonTypeName("koto_laajaalainenosaaminen")
public class KotoLaajaAlainenOsaaminenDto extends PerusteenOsaDto.Laaja {

    private LokalisoituTekstiDto yleiskuvaus;
    private List<KotoLaajaAlaisenOsaamisenAlueDto> osaamisAlueet = new ArrayList<>();

    @Override
    public String getOsanTyyppi() {
        return "koto_laajaalainenosaaminen";
    }
}
