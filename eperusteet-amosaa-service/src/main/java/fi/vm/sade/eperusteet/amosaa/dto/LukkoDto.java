package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.Lukko;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Data
@EqualsAndHashCode
public class LukkoDto {

    public LukkoDto(Lukko lukko) {
        this(lukko, null);
    }

    public LukkoDto(Lukko lukko, Integer revisio) {
        this.haltijaOid = lukko.getHaltijaOid();
        this.haltijaNimi = "";
        this.luotu = lukko.getLuotu();
        this.vanhentuu = lukko.getVanhentuu();
        this.oma = lukko.isOma();
        this.revisio = revisio;
        this.vanhentunut = this.vanhentuu.isBefore(new Date().toInstant());
    }

    final String haltijaOid;

    @Setter
    String haltijaNimi;
    final Instant luotu;
    final Instant vanhentuu;
    final Boolean oma;
    final boolean vanhentunut;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer revisio;

    public static LukkoDto of(Lukko lukko) {
        return lukko == null ? null : new LukkoDto(lukko);
    }

    public static LukkoDto of(Lukko lukko, int revisio) {
        return lukko == null ? null : new LukkoDto(lukko, revisio);
    }
}
