package fi.vm.sade.eperusteet.amosaa.service.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.PalauteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TiedoteQueryDto;
import java.util.Date;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@PreAuthorize("permitAll()") // OK, koska mäppääntyy julkisiin rajapintoihin
public interface EperusteetClient {
    String getPerusteData(Long id);

    <T> T getPeruste(Long id, Class<T> type);

    <T> T getPerusteOrNull(Long id, Class<T> type);

    <T> T getPeruste(String diaariNumero, Class<T> type);

    PerusteDto getYleinenPohja();

    String getYleinenPohjaSisalto();

    List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit);

    JsonNode getTiedotteet(Long jalkeen);

    JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto);

    ArviointiasteikkoDto getArviointiasteikko(Long id);

    JsonNode findFromPerusteet(Map<String, String> query);

    @PreAuthorize("permitAll()")
    PalauteDto lahetaPalaute(PalauteDto palaute) throws JsonProcessingException;

    Date getViimeisinJulkaisuPeruste(Long perusteId);

}
