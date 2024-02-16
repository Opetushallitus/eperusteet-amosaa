package fi.vm.sade.eperusteet.amosaa.dto;

import java.util.Date;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisionDto {
    private Integer numero;
    private Date pvm;
    private String muokkaajaOid;
    private String kommentti;
}
