package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Set;

@Converter
public class KoulutuksetConverter implements AttributeConverter<Set, String> {
    private static final Logger logger = LoggerFactory.getLogger(KoulutuksetConverter.class);

    private ObjectMapper objMapper;

    public KoulutuksetConverter() {
        objMapper = new ObjectMapper();
        objMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    public String convertToDatabaseColumn(Set value) {
        if (value == null) {
            return null;
        }

        try {
            return objMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            logger.error(ex.getLocalizedMessage());
            return null;
        }
    }

    public Set<KoulutusDto> convertToEntityAttribute(String value) {
        if ( value == null ) {
            return null;
        }

        Set<KoulutusDto> koulutukset = null;
        try {
            koulutukset = objMapper.readValue(value,
                    objMapper.getTypeFactory().constructCollectionType(Set.class, KoulutusDto.class));
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return koulutukset;
    }
}
