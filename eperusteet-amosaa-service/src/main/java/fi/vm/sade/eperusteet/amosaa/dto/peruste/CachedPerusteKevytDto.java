package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedPerusteKevytDto {
    private String diaarinumero;
    private Long perusteId;
    private LokalisoituTekstiDto nimi;
    private KoulutusTyyppi koulutustyyppi;
}
