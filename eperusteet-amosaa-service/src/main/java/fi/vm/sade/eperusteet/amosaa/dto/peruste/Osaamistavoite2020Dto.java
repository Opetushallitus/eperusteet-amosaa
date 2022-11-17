package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.utils.domain.utils.Kieli;
import fi.vm.sade.eperusteet.utils.dto.utils.LokalisoituTekstiDto;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Osaamistavoite2020Dto {
    private Long id;
    private Ammattitaitovaatimukset2019Dto tavoitteet;
    private LokalisoituTekstiDto nimi;
    private boolean pakollinen;
    private BigDecimal laajuus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Kieli kieli;
    private KoodiDto koodi;
    private String koodiUri;
    private String koodiArvo;

    public String getKoodiUri() {
        KoodiDto koodi = this.getKoodi();
        if (koodi != null) {
            return koodi.getUri();
        } else {
            return koodiUri;
        }
    }

    public String getKoodiArvo() {
        KoodiDto koodi = this.getKoodi();
        if (koodi != null) {
            return koodi.getArvo();
        } else {
            return koodiArvo;
        }
    }
}
