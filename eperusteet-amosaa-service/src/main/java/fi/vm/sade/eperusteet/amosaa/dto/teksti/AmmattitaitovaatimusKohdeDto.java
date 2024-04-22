package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmmattitaitovaatimusKohdeDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto selite;
    private List<AmmattitaitovaatimusDto> vaatimukset;
}
