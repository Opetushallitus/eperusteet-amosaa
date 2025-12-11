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
    private LokalisoituTekstiDto nimi;

    public void setMetadata(KoodistoMetadataDto[] metadata) {
        this.metadata = metadata;

        if (metadata != null) {
            Map<String, String> lokalisoitu = Arrays.stream(metadata).collect(Collectors.toMap(KoodistoMetadataDto::getKieli, KoodistoMetadataDto::getNimi));
            this.nimi = new LokalisoituTekstiDto(lokalisoitu);
        }
    }
}
