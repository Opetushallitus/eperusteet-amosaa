package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    @Override
    public KoodistoKoodiDto addKoodi(KoodistoKoodiDto koodi) {
        return koodi;
    }

    @Override
    public KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi) {
        return null;
    }

    @Override
    public KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi, long seuraavaKoodi) {
        return KoodistoKoodiDto.builder()
                .koodisto(KoodistoDto.of(koodistonimi))
                .koodiUri(koodistonimi + "_" + seuraavaKoodi)
                .build();
    }

    @Override
    public long nextKoodiId(String koodistonimi) {
        return 0;
    }

    @Override
    public Collection<Long> nextKoodiId(String koodistonimi, int count) {
        return IntStream.range(0, count).boxed().map(operand -> new Long(operand)).collect(Collectors.toList());
    }
}
