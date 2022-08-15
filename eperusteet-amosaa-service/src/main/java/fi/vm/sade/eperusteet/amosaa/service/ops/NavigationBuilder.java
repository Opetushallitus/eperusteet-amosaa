package fi.vm.sade.eperusteet.amosaa.service.ops;


import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NavigationBuilder extends OpetussuunnitelmaToteutus {
    @Override
    default Class getImpl() {
        return NavigationBuilder.class;
    }

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    default NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        throw new BusinessRuleViolationException("ei-tuettu");
    }

    default String getSisaltoviiteMetaKoodi(SisaltoViiteKevytDto sisaltoviite) {
        return null;
    }
}
