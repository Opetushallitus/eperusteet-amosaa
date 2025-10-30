package fi.vm.sade.eperusteet.amosaa.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.geneerinenarviointiasteikko.GeneerinenArviointiasteikkoKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@PreAuthorize("permitAll()") // OK, koska mäppääntyy julkisiin rajapintoihin
public interface EperusteetService {
    @PreAuthorize("isAuthenticated()")
    CachedPerusteBaseDto getCachedPeruste(PerusteBaseDto peruste);

    JsonNode getTutkinnonOsat(Long id);

    List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat);

    JsonNode getSuoritustavat(Long id);

    List<RakenneModuuliTunnisteDto> getSuoritustavat(Long ktId, Long opetussuunnitelmaId);

    RakenneModuuliTunnisteDto getYksittaisenRakenteenSuoritustavat(SuoritustapaLaajaDto suoritustapaLaajaDto, SisaltoViiteDto sisaltoViite);

    JsonNode getTutkinnonOsa(Long id, Long tosaId);

    JsonNode getSuoritustapa(Long id, String tyyppi);

    JsonNode getTutkinnonOsaViite(Long id, String tyyppi, Long tosaId);

    JsonNode getTutkinnonOsaViitteet(Long id, String tyyppi);

    <T> T getPerusteSisalto(Long cperusteId, Class<T> type);

    <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type);

    List<PerusteKevytDto> findPerusteet(Set<String> koulutustyypit, String nimi, String kieli);

    <T> T getPerusteSisaltoByPerusteId(Long perusteId, Class<T> type);

    Set<UUID> getRakenneTunnisteet(Long id, String suoritustapa);

    JsonNode getTiedotteet(Long jalkeen);

    JsonNode getGeneeriset();

    GeneerinenArviointiasteikkoKaikkiDto getGeneerinen(Long id);

    JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto);

    byte[] getLiite(Long perusteId, UUID id);

    PerusteenOsaDto getPerusteenOsa(Long perusteId, Long perusteenOsaId);

    PerusteKaikkiDto getPerusteKaikki(Long perusteCacheId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    PerusteDto getKoulutuskoodillaKorvaavaPeruste(@P("ktId") Long ktId, Long opsId);
}
