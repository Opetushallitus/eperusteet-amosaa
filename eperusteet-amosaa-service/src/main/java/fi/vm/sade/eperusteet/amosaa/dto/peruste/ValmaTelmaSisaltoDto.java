package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("valmaTelmaSisalto")
public class ValmaTelmaSisaltoDto {
    private Long id;
    private OsaamisenArviointiDto osaamisenarviointi;
    private LokalisoituTekstiDto osaamisenarviointiTekstina;
    private List<OsaamisenTavoiteDto> osaamistavoite;
}
