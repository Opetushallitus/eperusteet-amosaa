package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaTilastoDto {
    public static SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");

    private Long id;
    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private Date luotu;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Set<Kieli> julkaisukielet;
    private Set<JulkaisuKevytDto> julkaisut;
    private KoulutusTyyppi koulutustyyppi;
    private JotpaTyyppi jotpatyyppi;

    @JsonIgnore
    private CachedPerusteKevytDto peruste;

    public KoulutusTyyppi getKoulutustyyppi() {
        if (peruste != null) {
            return peruste.getKoulutustyyppi();
        }
        return koulutustyyppi;
    }

    public LokalisoituTekstiDto getPerusteNimi() {
        if (peruste != null) {
            return peruste.getNimi();
        }

        return null;
    }

    public Long getPerusteId() {
        if (peruste != null) {
            return peruste.getPerusteId();
        }

        return null;
    }

    public Integer getJulkaisuVuosi() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream()
                    .sorted(Comparator.comparing(JulkaisuKevytDto::getLuotu))
                    .map(julkaisu -> Integer.parseInt(getYearFormat.format(julkaisu.getLuotu())))
                    .findFirst()
                    .orElse(null);
        }

        if (tila.equals(Tila.JULKAISTU) && voimaantulo != null) {
            return Integer.parseInt(getYearFormat.format(voimaantulo));
        }

        return null;
    }

    public Date getJulkaistu() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream().max(Comparator.comparing(JulkaisuKevytDto::getLuotu)).map(JulkaisuKevytDto::getLuotu).orElse(null);
        }
        return null;
    }

    public Date getEnsijulkaisu() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream().min(Comparator.comparing(JulkaisuKevytDto::getLuotu)).map(JulkaisuKevytDto::getLuotu).orElse(null);
        }
        return null;
    }
}
