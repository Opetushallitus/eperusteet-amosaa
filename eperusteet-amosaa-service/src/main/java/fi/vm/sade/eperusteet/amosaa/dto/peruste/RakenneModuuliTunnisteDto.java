package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import java.util.ArrayList;
import java.util.Set;

import lombok.*;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RakenneModuuliTunnisteDto extends RakenneModuuliDto{

    private Set<String> koodit;
    
    public static RakenneModuuliTunnisteDto of(AbstractRakenneOsaDto rakenneModuuliDto, SuorituspolkuRiviDto suorituspolkuRiviDto) {
        
        RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto = new RakenneModuuliTunnisteDto();
        BeanUtils.copyProperties(rakenneModuuliDto, rakenneModuuliTunnisteDto, "osat");
        
        if(suorituspolkuRiviDto != null) {
            rakenneModuuliTunnisteDto.setKuvaus(suorituspolkuRiviDto.getKuvaus());
            rakenneModuuliTunnisteDto.setKoodit(suorituspolkuRiviDto.getKoodit());
        }
        
        rakenneModuuliTunnisteDto.setOsat(new ArrayList<>());
        
        return rakenneModuuliTunnisteDto;
    }
}
