package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PerusteBaseDto implements Serializable {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private PerusteVersionDto globalVersion;
    private KoulutusTyyppi koulutustyyppi;
    private Set<Kieli> kielet;
    private LokalisoituTekstiDto kuvaus;
    private String diaarinumero;
    private Date voimassaoloAlkaa;
    private Date siirtymaPaattyy;
    private Date voimassaoloLoppuu;
    private Date muokattu;
    private String tila;
    private String tyyppi;
    private Set<String> korvattavatDiaarinumerot;
    private Set<KoulutusDto> koulutukset;
    private Set<KoodiDto> osaamisalat;
    private KoulutustyyppiToteutus toteutus;
    List<TutkintonimikeKoodiDto> tutkintonimikkeet;
}
