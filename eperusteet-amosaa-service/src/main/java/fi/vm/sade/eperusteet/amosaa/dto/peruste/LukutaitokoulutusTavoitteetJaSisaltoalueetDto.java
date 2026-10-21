package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
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
@JsonTypeName("lukutaitokoulutus_tavoitteetjasisaltoalueet")
public class LukutaitokoulutusTavoitteetJaSisaltoalueetDto extends PerusteenOsaDto.Laaja {

    private List<LukutaitokoulutusTavoitealueDto> tavoitealueet = new ArrayList<>();

    @Override
    public String getOsanTyyppi() {
        return "lukutaitokoulutus_tavoitteetjasisaltoalueet";
    }
}
