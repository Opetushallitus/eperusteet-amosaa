package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class JulkaisuDto extends JulkaisuBaseDto {
    private JulkaisuDataDto data;
}
