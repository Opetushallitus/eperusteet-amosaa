package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import lombok.Data;

@Data
public class TutkintoonvalmentavaSisaltoDto {
    private Long id;
    private PerusteenOsaViiteDto.Laaja sisalto;
}
