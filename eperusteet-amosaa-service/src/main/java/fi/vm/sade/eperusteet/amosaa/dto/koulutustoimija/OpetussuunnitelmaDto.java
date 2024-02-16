package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaDto extends OpetussuunnitelmaBaseDto {
    private String kommentti;
    private Set<Kieli> julkaisukielet;
    private Set<LiiteDto> liitteet;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private Date voimassaoloLoppuu;
    private String hyvaksyja;
    private String paatosnumero;
    private String suoritustapa;
    private boolean esikatseltavissa;
    private Set<String> tutkintonimikkeet = new HashSet<>();
    private Set<String> osaamisalat = new HashSet<>();
    private String oppilaitosTyyppiKoodiUri;
    private KoodistoKoodiDto oppilaitosTyyppiKoodi;
    private KoulutusTyyppi koulutustyyppi;
    private Date viimeisinJulkaisuAika;
    private Date perustePaivitettyPvm;
}
