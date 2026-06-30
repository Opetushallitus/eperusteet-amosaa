package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sivutettu hakutulos.")
public class SivutettuTulosDto<T> implements Serializable {

    @Schema(description = "Hakutulosten lista.")
    private List<T> data;

    @Schema(description = "Nykyisen sivun numero (0-indeksoitu).")
    private int sivu;

    @Schema(description = "Sivujen kokonaismäärä.")
    private int sivuja;

    @JsonProperty("kokonaismäärä")
    @Schema(description = "Tulosten kokonaismäärä.")
    private long kokonaismaara;

    @Schema(description = "Yhdellä sivulla palautettavien tulosten määrä.")
    private int sivukoko;

    public static <T> SivutettuTulosDto<T> of(Page<T> page) {
        return new SivutettuTulosDto<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}
