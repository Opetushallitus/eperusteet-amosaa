package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import java.util.Set;

import lombok.*;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaLuontiDto extends OpetussuunnitelmaDto {
    private Long opsId;
    private Set<String> tutkinnonOsaKoodiIncludes;
}
