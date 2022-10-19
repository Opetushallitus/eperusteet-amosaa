package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutkintoonvalmentavaSisaltoDto {
    private Long id;
    private PerusteenOsaViiteDto.Laaja sisalto;
}
