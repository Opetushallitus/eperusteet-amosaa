package fi.vm.sade.eperusteet.amosaa.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
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

    public LokalisoituTekstiDto getNimi() {
        if (metadata == null) {
            return null;
        }

        Map<String, String> lokalisoitu = Arrays.stream(metadata)
                .filter(meta -> meta.getKieli() != null)
                .collect(Collectors.toMap(KoodistoMetadataDto::getKieli, KoodistoMetadataDto::getNimi, (a, b) -> a));
        return new LokalisoituTekstiDto(lokalisoitu);
    }
}
