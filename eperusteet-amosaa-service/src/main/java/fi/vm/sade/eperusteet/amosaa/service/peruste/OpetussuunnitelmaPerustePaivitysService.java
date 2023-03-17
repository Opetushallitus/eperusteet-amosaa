package fi.vm.sade.eperusteet.amosaa.service.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaToteutus;

public interface OpetussuunnitelmaPerustePaivitysService extends OpetussuunnitelmaToteutus {

    @Override
    default Class getImpl() {
        return OpetussuunnitelmaPerustePaivitysService.class;
    }

    default void paivitaOpetussuunnitelma(Long opetussuunnitelmaId, PerusteKaikkiDto perusteKaikkiDto) {
    };
}
