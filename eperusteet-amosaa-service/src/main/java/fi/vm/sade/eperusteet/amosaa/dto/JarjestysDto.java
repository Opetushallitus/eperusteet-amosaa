package fi.vm.sade.eperusteet.amosaa.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JarjestysDto {
    Long id;
    List<Long> lisaIdt;
    Integer jnro;
}
