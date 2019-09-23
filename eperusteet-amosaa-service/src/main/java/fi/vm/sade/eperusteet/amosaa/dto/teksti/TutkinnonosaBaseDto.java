package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TutkinnonosaBaseDto {
    private Long id;
    private TutkinnonosaTyyppi tyyppi;
    private String koodi;
}
