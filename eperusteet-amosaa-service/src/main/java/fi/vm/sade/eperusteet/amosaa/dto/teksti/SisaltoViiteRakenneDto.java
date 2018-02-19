package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author nkala
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteRakenneDto {
    private Long id;
    private List<SisaltoViiteRakenneDto> lapset;
}
