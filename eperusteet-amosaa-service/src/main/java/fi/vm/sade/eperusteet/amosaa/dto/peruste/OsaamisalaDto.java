package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsaamisalaDto {
    private LokalisoituTekstiDto nimi;
    private String osaamisalakoodiArvo;
    private String osaamisalakoodiUri;
}
