package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JulkaisuDataDto {
    private Long id;
    private int hash;
    private ObjectNode data;
}
