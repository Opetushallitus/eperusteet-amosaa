package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.VapaaTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OletusToteutusDto {
    private LokalisoituTekstiDto lahdeNimi;
    private LokalisoituTekstiDto otsikko;
    private TekstiosaDto tavatjaymparisto;
    private TekstiosaDto arvioinnista;
    private List<VapaaTekstiDto> vapaat = new ArrayList<>();
}
