package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JulkaisuDataDto {
    private Long id;
    private int hash;
    private ObjectNode data;
}
