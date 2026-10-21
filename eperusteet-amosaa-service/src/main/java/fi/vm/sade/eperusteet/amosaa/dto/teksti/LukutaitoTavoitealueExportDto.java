package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.LukutaitokoulutusTavoitteetJaSisaltoalueetDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LukutaitoTavoitealueExportDto extends LukutaitoTavoitealueDto {
    private LukutaitokoulutusTavoitteetJaSisaltoalueetDto perusteenOsa;
}
