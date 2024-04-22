package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractRakenneOsaDto {
    private LokalisoituTekstiDto kuvaus;
    private KoodiDto vieras;
    private UUID tunniste;
    private Boolean pakollinen;

    public final void foreach(final Visitor visitor) {
        foreach(visitor, 0);
    }

    protected abstract void foreach(final Visitor visitor, final int currentDepth);

    public interface Visitor {
        void visit(final AbstractRakenneOsaDto dto, final int depth);
    }

}
