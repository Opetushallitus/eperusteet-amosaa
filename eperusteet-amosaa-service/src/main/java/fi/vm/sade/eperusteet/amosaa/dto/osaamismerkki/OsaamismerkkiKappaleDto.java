package fi.vm.sade.eperusteet.amosaa.dto.osaamismerkki;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsaamismerkkiKappaleDto {
    private Long id;
    private LokalisoituTekstiDto kuvaus;
    private List<OsaamismerkkiKoodiDto> osaamismerkkiKoodit;

    public OsaamismerkkiKappaleDto(LokalisoituTekstiDto kuvaus, List<OsaamismerkkiKoodiDto> osaamismerkkiKoodit) {
        this.kuvaus = kuvaus;
        this.osaamismerkkiKoodit = osaamismerkkiKoodit;
    }
}
