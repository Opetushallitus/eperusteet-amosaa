package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class KotoLaajaAlainenOsaaminenExportDto extends KotoLaajaAlainenOsaaminenDto {
    private fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto perusteenOsa;
}
