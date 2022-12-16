package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmaValidationService extends OpetussuunnitelmaToteutus {

    @Override
    default Class getImpl() {
        return OpetussuunnitelmaValidationService.class;
    }

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    default Validointi validoi(@P("ktId") Long ktId, @P("opsId") Long opsId) {
        return null;
    }
}
