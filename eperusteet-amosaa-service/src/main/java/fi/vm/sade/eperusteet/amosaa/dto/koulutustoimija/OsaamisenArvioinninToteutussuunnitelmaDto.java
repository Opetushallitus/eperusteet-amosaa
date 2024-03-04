package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OsaamisenArvioinninToteutussuunnitelmaDto {
    private Long id;
    private OpetussuunnitelmaKevytDto opetussuunnitelma;
    private OpetussuunnitelmaKevytDto oatOpetussuunnitelma;
    private LokalisoituTekstiDto nimi;
    private Map<Kieli, String> url;
}
