package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface KoodistoService {

    @PreAuthorize("isAuthenticated()")
    List<KoodistoKoodiDto> getAll(String koodisto);

    @PreAuthorize("permitAll()")
    KoodistoKoodiDto get(String koodisto, String koodi);

    @PreAuthorize("isAuthenticated()")
    List<KoodistoKoodiDto> filterBy(String koodisto, String haku);

    @PreAuthorize("isAuthenticated()")
    List<KoodistoKoodiDto> getAlarelaatio(String koodi);

    @PreAuthorize("isAuthenticated()")
    List<KoodistoKoodiDto> getYlarelaatio(String koodi);
}
