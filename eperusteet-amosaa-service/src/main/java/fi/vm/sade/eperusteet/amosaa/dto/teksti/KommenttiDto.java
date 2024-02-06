package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KommenttiDto {
    private Long id;
    private Date luotu;
    private String luoja;
    private Date muokattu;
    private String muokkaaja;
    private Long tekstikappaleviiteId;
    private Long parentId;
    private Boolean poistettu;
    private String sisalto;
    private String nimi;
}
