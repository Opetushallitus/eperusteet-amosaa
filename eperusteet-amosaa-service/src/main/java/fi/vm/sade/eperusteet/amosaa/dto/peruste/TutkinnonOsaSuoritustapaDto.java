package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TutkinnonOsaSuoritustapaDto extends PerusteenOsaDto.Suppea {
    private LokalisoituTekstiDto kuvaus;
    private String koodiUri;
    private String koodiArvo;
    private TutkinnonOsaTyyppi tyyppi;
}
