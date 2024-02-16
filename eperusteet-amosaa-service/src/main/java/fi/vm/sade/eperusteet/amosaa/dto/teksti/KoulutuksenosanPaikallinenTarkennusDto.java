package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KoulutuksenosanPaikallinenTarkennusDto {

    private Long id;
    private LokalisoituTekstiDto tavoitteetKuvaus;
    private List<LokalisoituTekstiDto> tavoitteet = new ArrayList<>();
    private List<KoulutuksenosaLaajaalainenOsaaminenDto> laajaalaisetosaamiset = new ArrayList<>();
    private LokalisoituTekstiDto keskeinenSisalto;
    private LokalisoituTekstiDto arvioinninKuvaus;
    private List<KoulutuksenJarjestajaDto> koulutuksenJarjestajat = new ArrayList<>();
}
