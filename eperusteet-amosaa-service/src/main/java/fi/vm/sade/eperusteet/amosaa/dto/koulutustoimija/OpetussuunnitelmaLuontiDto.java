package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpetussuunnitelmaLuontiDto extends OpetussuunnitelmaDto {
    private Long opsId;
    private Set<String> tutkinnonOsaKoodiIncludes;
}
