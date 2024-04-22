package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class RakenneOsaDto extends AbstractRakenneOsaDto {
    private String erikoisuus;
    @JsonProperty("_tutkinnonOsaViite")
    private Long tutkinnonOsaViite;

    @Override
    protected void foreach(final Visitor visitor, final int depth) {
        visitor.visit(this, depth);
    }

}
