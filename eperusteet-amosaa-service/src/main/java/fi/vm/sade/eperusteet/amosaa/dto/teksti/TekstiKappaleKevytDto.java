package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;

import java.util.Date;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TekstiKappaleKevytDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private String muokkaaja;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String muokkaajanNimi;
    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private UUID tunniste;
    private Boolean pakollinen;
    private Boolean valmis;
}
