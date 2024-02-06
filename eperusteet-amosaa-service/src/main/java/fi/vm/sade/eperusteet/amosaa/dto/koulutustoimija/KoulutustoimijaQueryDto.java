package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaQueryDto extends QueryDto {
    private String organisaatio;
    private boolean organisaatioRyhma;
    private List<KoulutusTyyppi> koulutustyyppi;
}
