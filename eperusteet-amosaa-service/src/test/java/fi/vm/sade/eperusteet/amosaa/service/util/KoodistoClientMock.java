package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
public class KoodistoClientMock implements KoodistoClient {
    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        return null;
    }

    @Override
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> queryByKoodi(String koodisto, String koodi) {
        return null;
    }

    @Override
    public KoodistoKoodiDto getByUri(String uri) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> filterBy(String koodisto, String haku) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> getAlarelaatio(String koodi) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        return null;
    }
}
