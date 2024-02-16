package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaYstavaDto extends KoulutustoimijaBaseDto {
    YstavaStatus status;
}
