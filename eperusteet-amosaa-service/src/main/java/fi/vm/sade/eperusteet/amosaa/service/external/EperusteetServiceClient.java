package fi.vm.sade.eperusteet.amosaa.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@PreAuthorize("permitAll()") // OK, koska mäppääntyy julkisiin rajapintoihin
public interface EperusteetServiceClient {
    String getPerusteData(Long id);

    <T> T getPeruste(Long id, Class<T> type);

    <T> T getPeruste(String diaariNumero, Class<T> type);

    PerusteDto getYleinenPohja();

    String getYleinenPohjaSisalto();

    List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit);

    JsonNode getTiedotteet(Long jalkeen);

    ArviointiasteikkoDto getArviointiasteikko(Long id);
}