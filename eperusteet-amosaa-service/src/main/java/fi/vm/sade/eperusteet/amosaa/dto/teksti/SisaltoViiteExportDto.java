package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Käytetään sisällön viemiseen rakenteisena
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteExportDto extends SisaltoViiteExportBaseDto {
    private LokalisoituTekstiDto perusteteksti;
    private TutkinnonosaBaseDto tosa;
    private SuorituspolkuBaseDto suorituspolku;
    private TekstiKappaleJulkinenDto tekstiKappale;
    private List<SisaltoViiteExportDto> lapset;
}
