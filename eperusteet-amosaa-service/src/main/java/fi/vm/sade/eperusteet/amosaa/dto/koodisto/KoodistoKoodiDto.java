package fi.vm.sade.eperusteet.amosaa.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KoodistoKoodiDto {
    private String koodiUri;
    private String koodiArvo;
    private Date voimassaAlkuPvm;
    private Date voimassaLoppuPvm;
    private KoodistoDto koodisto;
    private KoodistoMetadataDto[] metadata;
}
