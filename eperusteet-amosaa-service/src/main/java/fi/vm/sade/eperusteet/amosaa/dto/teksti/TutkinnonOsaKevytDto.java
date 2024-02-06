package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import lombok.*;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutkinnonOsaKevytDto {
    private TutkinnonosaTyyppi tyyppi;
    private OmaTutkinnonosaKevytDto omatutkinnonosa;
    private Date muokattu;
    private Long perusteentutkinnonosa;
}
