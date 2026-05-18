package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.*;
import lombok.experimental.Tolerate;

import org.apache.commons.collections.CollectionUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaJulkaistuQueryDto extends QueryDto {
    private List<String> perusteenDiaarinumero;
    private List<Long> perusteId;
    private List<String> organisaatio;
    private List<String> tyyppi;
    private String oppilaitosTyyppiKoodiUri = "";
    private List<KoulutusTyyppi> koulutustyyppi = new ArrayList<>();
    private String nimi = "";
    private int sivukoko = 10;
    private List<String> jotpatyyppi = new ArrayList<>();
    private Boolean paikallistasisaltoa;

    public List<String> getTyyppi() {
        if (tyyppi == null) {
            return Arrays.asList(Tyyppi.OPS.toString());
        }

        return tyyppi;
    }

    public List<String> getJotpatyyppi() {
        if (CollectionUtils.isEmpty(jotpatyyppi)) {
            return List.of();
        }

        return jotpatyyppi;
    }

    @Tolerate
    public void setPerusteid(List<Long> perusteid) {
      this.perusteId = perusteid;
    }

    @Tolerate
    public void setPerusteendiaarinumero(List<String> perusteenDiaarinumero) {
      this.perusteenDiaarinumero = perusteenDiaarinumero;
    }

    @Tolerate
    public void setOppilaitostyyppikoodiuri(String oppilaitosTyyppiKoodiUri) {
      this.oppilaitosTyyppiKoodiUri = oppilaitosTyyppiKoodiUri;
    }
}
