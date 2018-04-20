package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonosaDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpetussuunnitelmaKaikkiDto extends OpetussuunnitelmaDto {
    SisaltoViiteExportDto sisalto;
    List<SuorituspolkuRakenneDto> suorituspolut;
    List<TutkinnonosaExportDto> tutkinnonOsat;
}
