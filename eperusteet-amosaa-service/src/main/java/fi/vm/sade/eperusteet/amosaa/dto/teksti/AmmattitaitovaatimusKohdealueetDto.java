package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmmattitaitovaatimusKohdealueetDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private List<AmmattitaitovaatimusKohdeDto> vaatimuksenKohteet;
}
