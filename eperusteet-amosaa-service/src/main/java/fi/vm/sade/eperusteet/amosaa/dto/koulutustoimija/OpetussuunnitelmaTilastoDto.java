package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

@Data
public class OpetussuunnitelmaTilastoDto {
    public static SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");

    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Set<Kieli> julkaisukielet;
    private Set<JulkaisuKevytDto> julkaisut;

    @JsonIgnore
    private CachedPerusteKevytDto peruste;

    public KoulutusTyyppi getKoulutustyyppi() {
        if (peruste != null) {
            return peruste.getKoulutustyyppi();
        }

        return null;
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

        if (tila.equals(Tila.JULKAISTU)) {
            return Integer.parseInt(getYearFormat.format(voimaantulo));
        }

        return null;
    }
}
