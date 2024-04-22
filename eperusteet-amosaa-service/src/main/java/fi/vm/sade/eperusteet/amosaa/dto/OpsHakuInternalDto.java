package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpsHakuInternalDto extends QueryDto {
    private List<Long> koulutustoimijat;
    private List<String> koulutustyyppi;
    private OpsTyyppi tyyppi;
    private Boolean jotpa;
    private Boolean julkaistuTaiValmis;
    private boolean poistunut = false;
}
