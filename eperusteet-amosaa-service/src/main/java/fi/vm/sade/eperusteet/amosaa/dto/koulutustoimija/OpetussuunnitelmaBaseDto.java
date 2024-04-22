package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaBaseDto {

    private Long id;
    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private OpsTyyppi tyyppi;
    private LokalisoituTekstiDto kuvaus;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Date luotu;
    private Date muokattu;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Reference pohja;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long perusteId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String perusteDiaarinumero;

    private CachedPerusteBaseDto peruste;

    @Deprecated
    @ApiModelProperty(hidden = true)
    private Tila tila2016;

    private JotpaTyyppi jotpatyyppi;

}
