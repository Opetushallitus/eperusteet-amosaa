package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class KotoKielitaitotasoExportDto extends KotoKielitaitotasoDto {
    private fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoKielitaitotasoDto perusteenOsa;
}
