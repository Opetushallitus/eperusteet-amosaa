package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;

import java.util.Date;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TutkinnonosaBaseDto implements KooditettuDto {
    private Long id;
    private TutkinnonosaTyyppi tyyppi;
    private String koodi;
    private Date muokattu;
    private VierasTutkinnonosaDto vierastutkinnonosa;
    private List<TutkinnonosaToteutusDto> toteutukset;
    private LokalisoituTekstiDto osaamisenOsoittaminen;
    private Long perusteentutkinnonosa;
    private List<VapaaTekstiDto> vapaat;
    private LokalisoituTekstiDto kooditettuNimi;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.kooditettuNimi = kooditettu;
    }
}
