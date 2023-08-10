package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArviointiExportDto {
    private Long id;
    private LokalisoituTekstiDto lisatiedot;
    private List<ArvioinninKohdealueExportDto> arvioinninKohdealueet;
}
