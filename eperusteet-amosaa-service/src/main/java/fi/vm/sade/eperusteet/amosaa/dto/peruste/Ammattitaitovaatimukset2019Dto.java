package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ammattitaitovaatimukset2019Dto {
    private Long id;
    private LokalisoituTekstiDto kohde;
    private List<Ammattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
    private List<AmmattitaitovaatimustenKohdealue2019Dto> kohdealueet = new ArrayList<>();
}
