package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TutkinnonOsaViiteSuppeaDto {
    private Long id;
    private BigDecimal laajuus;
    private BigDecimal laajuusMaksimi; // TODO: Ainoastaan valmatelmalla
    private Integer jarjestys;
    @JsonProperty("_tutkinnonOsa")
    private Long tutkinnonOsa;
}
