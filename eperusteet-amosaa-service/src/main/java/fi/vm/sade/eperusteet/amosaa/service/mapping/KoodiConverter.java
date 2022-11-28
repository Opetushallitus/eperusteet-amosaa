package fi.vm.sade.eperusteet.amosaa.service.mapping;

import fi.vm.sade.eperusteet.amosaa.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoodiDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

@Component
public class KoodiConverter extends BidirectionalConverter<KoodistoKoodi, KoodiDto> {
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
