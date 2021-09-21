package fi.vm.sade.eperusteet.amosaa.service.ops;


import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NavigationBuilderPublic extends OpetussuunnitelmaToteutus {
    @Override
    default Class getImpl() {
        return NavigationBuilderPublic.class;
    }

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    default NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        throw new BusinessRuleViolationException("ei-tuettu");
    }
}
