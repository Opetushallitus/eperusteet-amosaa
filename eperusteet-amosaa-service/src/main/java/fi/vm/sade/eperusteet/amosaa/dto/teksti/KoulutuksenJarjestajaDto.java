package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KoulutuksenJarjestajaDto {
    private Long id;
    private String oid;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto url;
    private LokalisoituTekstiDto kuvaus;
}
