package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VapaasivistystyoSisaltoDto {
    private PerusteenOsaViiteDto.Laaja sisalto;
    private Integer laajuus;
}
