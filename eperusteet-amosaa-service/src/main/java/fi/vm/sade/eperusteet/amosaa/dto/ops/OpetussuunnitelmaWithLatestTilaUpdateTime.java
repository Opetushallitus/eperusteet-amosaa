package fi.vm.sade.eperusteet.amosaa.dto.ops;

import java.util.Date;

public interface OpetussuunnitelmaWithLatestTilaUpdateTime {
    Long getId();
    Date getViimeisinTilaMuutosAika();
}
