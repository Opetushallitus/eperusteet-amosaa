package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TutkintonimikeKoodiDto {
    private String tutkinnonOsaUri;
    private String tutkinnonOsaArvo;
    private String osaamisalaUri;
    private String osaamisalaArvo;
    private String tutkintonimikeUri;
}
