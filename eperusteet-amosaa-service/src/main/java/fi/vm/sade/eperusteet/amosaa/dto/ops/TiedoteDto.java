package fi.vm.sade.eperusteet.amosaa.dto.ops;

import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiedoteDto {
    private Long id;
    private String otsikko;
    private String teksti;
    private Boolean julkinen;
    private Boolean tarkea;
    private Date luotu;
    private String luoja;
    private Date muokattu;
    private String muokkaaja;
}
