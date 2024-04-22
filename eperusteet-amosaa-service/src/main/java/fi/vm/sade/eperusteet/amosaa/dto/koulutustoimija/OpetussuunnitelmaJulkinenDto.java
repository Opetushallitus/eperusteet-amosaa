package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaJulkinenDto extends OpetussuunnitelmaBaseDto {
    private String kommentti;
    private Set<Kieli> julkaisukielet;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Set<LiiteDto> liitteet;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private String hyvaksyja;
    private String paatosnumero;
    private String suoritustapa;
    private Set<String> tutkintonimikkeet = new HashSet<>();
    private Set<String> osaamisalat = new HashSet<>();
}
