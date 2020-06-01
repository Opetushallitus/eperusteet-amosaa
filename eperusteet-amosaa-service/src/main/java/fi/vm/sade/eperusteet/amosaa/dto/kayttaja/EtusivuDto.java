package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtusivuDto {
    private Long toteutussuunnitelmatKeskeneraiset;
    private Long toteutussuunnitelmatJulkaistut;
    private Long ktYhteinenOsuusKeskeneraiset;
    private Long ktYhteinenOsuusJulkaistut;
}