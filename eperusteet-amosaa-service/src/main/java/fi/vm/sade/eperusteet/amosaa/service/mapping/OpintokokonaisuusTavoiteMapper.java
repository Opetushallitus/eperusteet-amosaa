package fi.vm.sade.eperusteet.amosaa.service.mapping;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTavoite;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LokalisointiService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpintokokonaisuusTavoiteMapper extends CustomMapper<OpintokokonaisuusTavoite, OpintokokonaisuusTavoiteDto> {

    @Lazy
    @Autowired
    private KoodistoClient koodistoClient;

    @Override
    public void mapBtoA(OpintokokonaisuusTavoiteDto opintokokonaisuusTavoiteDto, OpintokokonaisuusTavoite opintokokonaisuusTavoite, MappingContext context) {
        super.mapBtoA(opintokokonaisuusTavoiteDto, opintokokonaisuusTavoite, context);
    }

    @Override
    public void mapAtoB(OpintokokonaisuusTavoite opintokokonaisuusTavoite, OpintokokonaisuusTavoiteDto opintokokonaisuusTavoiteDto, MappingContext context) {
        super.mapAtoB(opintokokonaisuusTavoite, opintokokonaisuusTavoiteDto, context);

        if (opintokokonaisuusTavoite.getTavoiteKoodi() != null) {
            KoodistoKoodiDto koodistokoodi = koodistoClient.getByUri(opintokokonaisuusTavoite.getTavoiteKoodi());
            if (koodistokoodi != null) {
                Map<String, String> lokalisoitu = Arrays.stream(koodistokoodi.getMetadata()).collect(Collectors.toMap(KoodistoMetadataDto::getKieli, KoodistoMetadataDto::getNimi));
                opintokokonaisuusTavoiteDto.setTavoite(new LokalisoituTekstiDto(lokalisoitu));
            }
        }
    }
}
