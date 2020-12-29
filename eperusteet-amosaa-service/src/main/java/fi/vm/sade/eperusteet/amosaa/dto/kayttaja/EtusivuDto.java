package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtusivuDto {
    private Long toteutussuunnitelmatKeskeneraiset = 0l;
    private Long toteutussuunnitelmatJulkaistut = 0l;
    private Long ktYhteinenOsuusKeskeneraiset = 0l;
    private Long ktYhteinenOsuusJulkaistut = 0l;
    private Long toteutussuunnitelmaPohjatKeskeneraiset = 0l;
    private Long toteutussuunnitelmaPohjatValmiit = 0l;
}
