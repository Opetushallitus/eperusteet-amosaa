package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoviiteQueryDto extends QueryDto {
    private Long opetussuunnitelmaId;
    private SisaltoTyyppi tyyppi;
    private OpsTyyppi opsTyyppi;
    private boolean sortDesc = false;


}
