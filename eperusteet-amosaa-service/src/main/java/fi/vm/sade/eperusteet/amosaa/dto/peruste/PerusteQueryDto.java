package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerusteQueryDto {
    private int sivu = 0;
    private int sivukoko = 25;
    private boolean tuleva = true;
    private boolean siirtyma = true;
    private boolean voimassaolo = true;
    private boolean poistunut = true;
    private String nimi = "";
    private List<String> koulutustyyppi;
    private String koulutuskoodi;
    private String perusteTyyppi;
    private Long muokattu;
    private boolean tutkintonimikkeet = false;
    private boolean tutkinnonosat = false;
    private boolean osaamisalat = false;
    private boolean koulutusvienti = false;
    private long nykyinenAika = new Date().getTime();

    public void setTyyppi(List<String> tyyppi) {
        this.koulutustyyppi = tyyppi;
    }

    public List<String> getTyyppi() {
        return Stream.of(KoulutusTyyppi.values()).map(kt -> kt.toString()).collect(Collectors.toList());
    }

}
