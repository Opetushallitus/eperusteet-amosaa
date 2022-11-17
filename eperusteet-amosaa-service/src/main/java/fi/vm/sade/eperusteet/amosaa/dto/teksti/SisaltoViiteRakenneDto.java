package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import lombok.*;

import java.util.List;

/**
 * @author nkala
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteRakenneDto {
    private Long id;
    private Reference vanhempi;
    private List<SisaltoViiteRakenneDto> lapset;


}
