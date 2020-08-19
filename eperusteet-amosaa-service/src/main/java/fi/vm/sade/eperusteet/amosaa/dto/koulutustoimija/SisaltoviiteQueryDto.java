package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;
import lombok.Data;

@Data
public class SisaltoviiteQueryDto extends QueryDto {
    private Long opetussuunnitelmaId;
    private SisaltoTyyppi tyyppi;
    private OpsTyyppi opsTyyppi;
    private boolean sortDesc = false;


}
