package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KotoLaajaAlainenOsaaminenExportDto extends KotoLaajaAlainenOsaaminenDto {
    private fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto perusteenOsa;
}
