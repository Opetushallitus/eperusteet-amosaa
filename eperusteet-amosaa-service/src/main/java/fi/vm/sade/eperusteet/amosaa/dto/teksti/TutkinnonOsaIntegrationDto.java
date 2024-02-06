package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutkinnonOsaIntegrationDto {
    private Long id;
    private TutkinnonosaTyyppi tyyppi;
    private OmaTutkinnonosaIntegrationDto omatutkinnonosa;
}
