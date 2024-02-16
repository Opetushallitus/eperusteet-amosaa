package fi.vm.sade.eperusteet.amosaa.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Set;
import java.util.UUID;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuorituspolkuRiviDto extends SuorituspolkuRiviJulkinenDto {
    private UUID rakennemoduuli;
    private Long jrno;
    private Boolean piilotettu;

    static public SuorituspolkuRiviDto of(UUID moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus) {
        SuorituspolkuRiviDto result = new SuorituspolkuRiviDto();
        result.setRakennemoduuli(moduuli);
        result.setPiilotettu(piilotettu);
        result.setKuvaus(kuvaus);
        return result;
    }

    static public SuorituspolkuRiviDto of(String moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus) {
        return of(UUID.fromString(moduuli), piilotettu, kuvaus);
    }

    static public SuorituspolkuRiviDto of(UUID moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus, Set<String> koodit) {
        SuorituspolkuRiviDto suorituspolkuRiviDto = of(moduuli, piilotettu, kuvaus);
        suorituspolkuRiviDto.setKoodit(koodit);
        return suorituspolkuRiviDto;
    }

}
