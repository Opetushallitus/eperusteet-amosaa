package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import java.util.List;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpsHakuDto extends QueryDto {
    private Long koulutustoimija;
    private List<Long> koulutustoimijat;
    private Long peruste;
    private List<KoulutusTyyppi> koulutustyyppi;
    private Set<Tila> tila;
    private Set<OpsTyyppi> tyyppi;
    private boolean organisaatioRyhma;

    public void setTila(Tila tila) {
        if (this.tila == null) {
            this.tila = new HashSet<>();
        }
        this.tila.add(tila);
    }

    public void setTila(Set<Tila> tila) {
        this.tila = tila;
    }

    public void setTyyppi(OpsTyyppi tyyppi) {
        if (this.tyyppi == null) {
            this.tyyppi = new HashSet<>();
        }
        this.tyyppi.add(tyyppi);
    }

    public void setTyyppi(Set<OpsTyyppi> tyyppi) {
        this.tyyppi = tyyppi;
    }
}
