package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.Data;

@Data
public class KotoOpintoExportDto extends KotoOpintoDto {
    private fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto perusteenOsa;
}
