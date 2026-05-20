package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Opetussuunnitelman tiedot.")
public class OpetussuunnitelmaDto extends OpetussuunnitelmaBaseDto {
    @Schema(description = "Vapaa kommentti opetussuunnitelmasta.")
    private String kommentti;
    @Schema(description = "Opetussuunnitelman julkaistut kielet.")
    private Set<Kieli> julkaisukielet;
    @Schema(description = "Opetussuunnitelmaan liittyvät liitteet.")
    private Set<LiiteDto> liitteet;
    @Schema(description = "Opetussuunnitelman hyväksymispäivämäärä.")
    private Date paatospaivamaara;
    @Schema(description = "Opetussuunnitelman voimaantulopäivämäärä.")
    private Date voimaantulo;
    @Schema(description = "Opetussuunnitelman voimassaolon päättymispäivämäärä.")
    private Date voimassaoloLoppuu;
    @Schema(description = "Opetussuunnitelman hyväksyjän nimi.")
    private String hyvaksyja;
    @Schema(description = "Opetussuunnitelman päätösnumero.")
    private String paatosnumero;
    @Schema(description = "Suoritustapa (esim. opiskelu työssä oppimisena).")
    private String suoritustapa;
    @Schema(description = "Onko opetussuunnitelma esikatseltavissa ennen virallista julkaisua.")
    private boolean esikatseltavissa;
    @Schema(description = "Opetussuunnitelman tutkintonimikkeet.")
    private Set<String> tutkintonimikkeet = new HashSet<>();
    @Schema(description = "Opetussuunnitelman osaamisalat.")
    private Set<String> osaamisalat = new HashSet<>();
    @Schema(description = "Opetussuunnitelman koulutustyyppi.")
    private KoulutusTyyppi koulutustyyppi;
    @Schema(description = "Viimeisimmän julkaisun aikaleima.")
    private Date viimeisinJulkaisuAika;
    @Schema(description = "Perusteen viimeisin päivityspäivämäärä.")
    private Date perustePaivitettyPvm;
    @Schema(description = "Opetussuunnitelmaan liittyvät osaamisen arvioinnin toteutussuunnitelmat (OAT).")
    private List<OsaamisenArvioinninToteutussuunnitelmaDto> osaamisenArvioinninToteutussuunnitelmat;

    @Deprecated
    @Schema(description = "Oppilaitostyypin koodisto-URI.", deprecated = true)
    private String oppilaitosTyyppiKoodiUri;
    @Deprecated
    @Schema(description = "Oppilaitostyypin koodistoarvo.", deprecated = true)
    private KoodistoKoodiDto oppilaitosTyyppiKoodi;
}
