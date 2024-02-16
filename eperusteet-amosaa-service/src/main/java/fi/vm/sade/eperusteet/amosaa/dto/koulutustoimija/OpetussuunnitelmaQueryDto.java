package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaQueryDto extends QueryDto {
    private String perusteenDiaarinumero;
    private Long perusteId;
    private String organisaatio;
    private List<String> tyyppi;
    private boolean organisaatioRyhma;
    private String oppilaitosTyyppiKoodiUri;
    private List<KoulutusTyyppi> koulutustyyppi;

    public List<OpsTyyppi> getTyyppi() {
        if (tyyppi != null && !tyyppi.isEmpty()) {
            return tyyppi.stream()
                    .map(t -> OpsTyyppi.of(t.toUpperCase()))
                    .filter(t -> t != OpsTyyppi.POHJA)
                    .collect(Collectors.toList());
        } else {
            return Arrays.asList(OpsTyyppi.OPS);
        }
    }
}
