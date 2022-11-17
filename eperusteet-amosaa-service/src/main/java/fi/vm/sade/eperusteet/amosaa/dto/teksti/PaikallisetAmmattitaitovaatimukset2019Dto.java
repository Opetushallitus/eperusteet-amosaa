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
}
