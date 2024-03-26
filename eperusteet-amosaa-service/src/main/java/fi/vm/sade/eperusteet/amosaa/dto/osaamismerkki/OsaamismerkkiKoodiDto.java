package fi.vm.sade.eperusteet.amosaa.dto.osaamismerkki;

import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsaamismerkkiKoodiDto implements KooditettuDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private String koodi;
    private Date voimassaAlkuPvm;
    private Date voimassaLoppuPvm;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu, Date voimassaAlkuPvm, Date voimassaLoppuPvm) {
        this.nimi = kooditettu;
        this.voimassaAlkuPvm = voimassaAlkuPvm;
        this.voimassaLoppuPvm = voimassaLoppuPvm;
    }

    public OsaamismerkkiKoodiDto(LokalisoituTekstiDto nimi, String koodi) {
        this.nimi = nimi;
        this.koodi = koodi;
    }
}
