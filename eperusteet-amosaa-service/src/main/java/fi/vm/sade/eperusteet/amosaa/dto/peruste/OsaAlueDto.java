package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsaAlueDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private KoodiDto koodi;
    private String koodiUri;
    private String koodiArvo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Kieli kieli;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ValmaTelmaSisaltoDto valmaTelmaSisalto;
    private KoodiDto kielikoodi;
}
