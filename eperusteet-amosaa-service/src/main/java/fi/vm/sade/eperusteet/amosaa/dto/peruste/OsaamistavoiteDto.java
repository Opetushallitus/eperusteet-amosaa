package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OsaamistavoiteDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private boolean pakollinen;
    private BigDecimal laajuus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Kieli kieli;
}
