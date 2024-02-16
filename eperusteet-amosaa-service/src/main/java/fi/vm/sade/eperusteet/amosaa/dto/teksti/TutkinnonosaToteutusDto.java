package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Set;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TutkinnonosaToteutusDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private TekstiosaDto tavatjaymparisto;
    private TekstiosaDto arvioinnista;
    private Set<String> koodit;
    private List<VapaaTekstiDto> vapaat;
    private boolean oletustoteutus;
}
