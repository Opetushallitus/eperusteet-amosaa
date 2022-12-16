package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KotoKielitaitotasoDto {

    private Long id;
    private List<KotoTaitotasoDto> taitotasot = new ArrayList<>();
    private List<KotoTaitotasoLaajaAlainenOsaaminenDto> laajaAlaisetOsaamiset = new ArrayList<>();
}
