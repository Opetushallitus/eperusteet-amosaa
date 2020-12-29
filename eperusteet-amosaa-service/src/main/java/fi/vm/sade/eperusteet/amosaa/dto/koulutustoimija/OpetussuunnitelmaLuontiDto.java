package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpetussuunnitelmaLuontiDto extends OpetussuunnitelmaDto {
    Long opsId;
    Set<String> tutkinnonOsaKoodiIncludes;
}
