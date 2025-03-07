package fi.vm.sade.eperusteet.amosaa.service.mapping;

import fi.vm.sade.eperusteet.amosaa.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoodiDto;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KoodiConverter extends BidirectionalConverter<KoodistoKoodi, KoodiDto> {

    @Lazy
    @Autowired
    KoodistoClient koodistoClient;

    @Override
    public KoodiDto convertTo(KoodistoKoodi koodistoKoodi, Type<KoodiDto> type, MappingContext mappingContext) {
        if (koodistoKoodi != null) {
            KoodiDto result = new KoodiDto();
            result.setArvo(koodistoKoodi.getKoodiArvo());
            result.setUri(koodistoKoodi.getKoodiUri());
            result.setVersio(null);
            if (koodistoKoodi.getKoodiUri() != null) {
                result.setKoodisto(koodistoKoodi.getKoodiUri().split("_")[0]);
            }

            result.setNimi(koodistoClient.getNimi(koodistoKoodi.getKoodiUri()).asMap());
            return result;
        }
        return null;
    }

    @Override
    public KoodistoKoodi convertFrom(KoodiDto koodiDto, Type<KoodistoKoodi> type, MappingContext mappingContext) {
        if (koodiDto != null) {
            return new KoodistoKoodi(koodiDto.getUri(), koodiDto.getArvo());
        }
        return null;
    }
}
