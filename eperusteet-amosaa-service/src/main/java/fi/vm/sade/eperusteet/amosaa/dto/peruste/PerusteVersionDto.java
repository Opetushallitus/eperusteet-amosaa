package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import java.util.Date;

import lombok.*;

@Data
public class PerusteVersionDto {
    private Date aikaleima;

    public PerusteVersionDto() {
    }

    public PerusteVersionDto(Date aikaleima) {
        this.aikaleima = aikaleima;
    }
}
