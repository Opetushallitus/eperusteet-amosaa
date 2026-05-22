package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import lombok.*;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutkinnonOsaKevytDto implements KooditettuDto {
    private TutkinnonosaTyyppi tyyppi;
    private OmaTutkinnonosaKevytDto omatutkinnonosa;
    private Date muokattu;
    private Long perusteentutkinnonosa;
    private LokalisoituTekstiDto kooditettuNimi;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.kooditettuNimi = kooditettu;
    }
}
