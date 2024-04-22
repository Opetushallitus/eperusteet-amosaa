package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutkinnonosaDto extends TutkinnonosaBaseDto {
    private OmaTutkinnonosaDto omatutkinnonosa;
}
