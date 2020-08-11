package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Date;
import java.util.Set;
import lombok.Data;

@Data
public class OpetussuunnitelmaTilastoDto {
    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Set<Kieli> julkaisukielet;

    @JsonIgnore
    private CachedPerusteBaseDto peruste;

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
}
