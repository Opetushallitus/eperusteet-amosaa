package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaikallisetAmmattitaitovaatimukset2019Dto {
    private Long id;
    private LokalisoituTekstiDto kohde;
    private List<PaikallinenAmmattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
    private List<PaikallinenAmmattitaitovaatimustenKohdealue2019Dto> kohdealueet = new ArrayList<>();

    public void syncKoodiNimet() {
        for (PaikallinenAmmattitaitovaatimus2019Dto vaatimus : vaatimukset) {
            if (vaatimus.getKoodi() != null) {
                vaatimus.setVaatimus(new LokalisoituTekstiDto(vaatimus.getKoodi().getNimi()));
            }
        }

        for (PaikallinenAmmattitaitovaatimustenKohdealue2019Dto kohdealue : kohdealueet) {
            for (PaikallinenAmmattitaitovaatimus2019Dto vaatimus : kohdealue.getVaatimukset()) {
                if (vaatimus.getKoodi() != null) {
                    vaatimus.setVaatimus(new LokalisoituTekstiDto(vaatimus.getKoodi().getNimi()));
                }
            }
        }
    }
}
