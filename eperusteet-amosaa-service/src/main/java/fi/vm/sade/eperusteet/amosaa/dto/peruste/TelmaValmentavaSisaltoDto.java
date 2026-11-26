package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelmaValmentavaSisaltoDto {
    private Long id;
    private PerusteenOsaViiteDto.Laaja sisalto;
}
