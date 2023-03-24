package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaViiteDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmaSisaltoCreateService {

    @PreAuthorize("hasPermission(#opetussuunnitelma.id, 'opetussuunnitelma', 'LUONTI')")
    void poistaPerusteenSisaltoOpetussuunnitelmalta(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteKaikkiDto perusteKaikkiDto);
    @PreAuthorize("hasPermission(#opetussuunnitelma.id, 'opetussuunnitelma', 'LUONTI')")
    void paivitaOpetussuunnitelmaPerusteenSisallolla(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteenOsaViiteDto.Laaja sisalto, PerusteenOsaViiteDto.Laaja parent);
    @PreAuthorize("hasPermission(#opetussuunnitelma.id, 'opetussuunnitelma', 'LUONTI')")
    void alustaOpetussuunnitelmaPerusteenSisallolla(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteenOsaViiteDto.Laaja sisalto);
}
