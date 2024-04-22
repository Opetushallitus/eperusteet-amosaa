package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerusteTekstiKappaleViiteDto {
    private Long id;
    private PerusteTekstiKappaleDto tesktiKappale;
    private List<PerusteTekstiKappaleViiteDto> lapset;
}
