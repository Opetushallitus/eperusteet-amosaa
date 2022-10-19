package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KotoOpintoExportDto extends KotoOpintoDto {
    private fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto perusteenOsa;
}
