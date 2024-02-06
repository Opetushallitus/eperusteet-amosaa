package fi.vm.sade.eperusteet.amosaa.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisaatioDto {
    private String oid;
    private List<String> tyypit;
    private LokalisoituTekstiDto nimi;
}
