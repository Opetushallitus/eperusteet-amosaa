package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmaCreateService extends OpetussuunnitelmaToteutus {

    @Override
    default Class getImpl() {
        return OpetussuunnitelmaCreateService.class;
    }

    @PreAuthorize("hasPermission(#koulutustoimija.id, 'koulutustoimija', 'LUONTI')")
    default OpetussuunnitelmaBaseDto create(Koulutustoimija koulutustoimija, OpetussuunnitelmaLuontiDto opsDto) {
        return null;
    }
}
