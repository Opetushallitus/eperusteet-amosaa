package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuorituspolkuBaseDto {
    private Long id;
}
