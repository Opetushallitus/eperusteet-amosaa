package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteBaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanhentunutPohjaperusteDto {
    OpetussuunnitelmaBaseDto opetussuunnitelma;
    PerusteBaseDto perusteUusi;
    PerusteBaseDto perusteVanha;
}
