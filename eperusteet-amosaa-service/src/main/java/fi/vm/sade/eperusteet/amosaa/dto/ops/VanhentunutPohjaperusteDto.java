package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteBaseDto;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanhentunutPohjaperusteDto {
    OpetussuunnitelmaBaseDto opetussuunnitelma;
    PerusteBaseDto perusteUusi;
    PerusteBaseDto perusteVanha;

    Date edellinenPaivitys;
    Date perustePaivittynyt;
}
