package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteDto extends PerusteBaseDto {
    Set<SuoritustapaDto> suoritustavat;

}
