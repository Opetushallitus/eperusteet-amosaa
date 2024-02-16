package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("permitAll()")
public interface KoodistoClient {
    List<KoodistoKoodiDto> getAll(String koodisto);

    KoodistoKoodiDto get(String koodisto, String koodi);

    List<KoodistoKoodiDto> queryByKoodi(String koodisto, String koodi);

    KoodistoKoodiDto getByUri(String uri);

    List<KoodistoKoodiDto> filterBy(String koodisto, String haku);

    List<KoodistoKoodiDto> getAlarelaatio(String koodi);

    List<KoodistoKoodiDto> getYlarelaatio(String koodi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodi(KoodistoKoodiDto koodi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi, long seuraavaKoodi);

    @PreAuthorize("isAuthenticated()")
    long nextKoodiId(String koodistonimi);

    @PreAuthorize("isAuthenticated()")
    Collection<Long> nextKoodiId(String koodistonimi, int count);
}
