package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class KoulutuksenJarjestajaDto {
    private Long id;
    private String oid;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto url;
    private LokalisoituTekstiDto kuvaus;
}
