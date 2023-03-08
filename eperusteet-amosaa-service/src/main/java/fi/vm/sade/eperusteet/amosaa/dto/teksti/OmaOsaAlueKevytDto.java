
package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmaOsaAlueKevytDto implements KooditettuDto {
    private Long id;
    private OmaOsaAlueTyyppi tyyppi;
    private LokalisoituTekstiDto nimi;
    private Long perusteenOsaAlueId;
    private String perusteenOsaAlueKoodi;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.nimi = kooditettu;
    }

    public void setNimi(LokalisoituTekstiDto nimi) {
        if (nimi != null) {
            this.nimi = nimi;
        }
    }

    @JsonIgnore
    public String getKoodiArvo() {
        return perusteenOsaAlueKoodi != null ? perusteenOsaAlueKoodi.split("_")[1] : null;
    }
}