package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Opetussuunnitelman perustiedot.")
public class OpetussuunnitelmaBaseDto {

    @Schema(description = "Opetussuunnitelman tunniste.")
    private Long id;
    @Schema(description = "Opetussuunnitelman nimi (lokalisoitu).")
    private LokalisoituTekstiDto nimi;
    @Schema(description = "Opetussuunnitelman tila (esim. luonnos, valmis, julkaistu).")
    private Tila tila;
    @Schema(description = "Opetussuunnitelman tyyppi (esim. ops, yhteinen, yleinen).")
    private OpsTyyppi tyyppi;
    @Schema(description = "Opetussuunnitelman kuvaus (lokalisoitu).")
    private LokalisoituTekstiDto kuvaus;
    @Schema(description = "Opetussuunnitelman koulutustoimija.")
    private KoulutustoimijaBaseDto koulutustoimija;
    @Schema(description = "Opetussuunnitelman luontiaikaleima.")
    private Date luotu;
    @Schema(description = "Opetussuunnitelman viimeisin muokkausaikaleima.")
    private Date muokattu;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Viite opetussuunnitelman pohjaan.")
    private Reference pohja;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Perusteen tunniste ePerusteet-palvelussa (perusteId).")
    private Long perusteId;
    
    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Perusteen diaarinumero.", deprecated = true)
    private String perusteDiaarinumero;

    @Schema(description = "Opetussuunnitelman perusteen tiedot.")
    private CachedPerusteBaseDto peruste;

    @Deprecated
    @Schema(hidden = true)
    private Tila tila2016;

    @Schema(description = "JOTPA-tyyppi (esim. VST, MUU).")
    private JotpaTyyppi jotpatyyppi;

}
