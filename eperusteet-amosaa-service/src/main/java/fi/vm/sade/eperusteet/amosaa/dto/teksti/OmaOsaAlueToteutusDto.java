package fi.vm.sade.eperusteet.amosaa.dto.teksti;

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
public class OmaOsaAlueToteutusDto {
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto tavatjaymparisto;
    private LokalisoituTekstiDto arvioinnista;
    private List<VapaaTekstiDto> vapaat = new ArrayList<>();
}
