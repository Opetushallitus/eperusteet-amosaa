package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KevytTekstiKappaleDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmaOsaAlueDto implements KooditettuDto {
    private Long id;
    private OmaOsaAlueTyyppi tyyppi;
    private LokalisoituTekstiDto nimi;
    private String perusteenOsaAlueKoodi;
    private Long perusteenOsaAlueId;
    private boolean piilotettu;
    private List<OmaOsaAlueToteutusDto> toteutukset = new ArrayList<>();
    private PaikallisetAmmattitaitovaatimukset2019Dto osaamistavoitteet = new PaikallisetAmmattitaitovaatimukset2019Dto();
    private Long geneerinenarviointi;
    private Integer laajuus;
    private String koodi;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private List<KevytTekstiKappaleDto> vapaat = new ArrayList<>();

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
