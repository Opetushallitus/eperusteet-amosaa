package fi.vm.sade.eperusteet.amosaa.dto.dokumentti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LokalisointiDto {
    String value;
    String key;
    Long id;
    String locale;
    String description;
    String category;
    Date created;
}
