package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KayttajaoikeusDto {
    private Long id;
    private Reference kayttaja;
    private Reference opetussuunnitelma;
    private Reference koulutustoimija;
    private KayttajaoikeusTyyppi oikeus;
}
