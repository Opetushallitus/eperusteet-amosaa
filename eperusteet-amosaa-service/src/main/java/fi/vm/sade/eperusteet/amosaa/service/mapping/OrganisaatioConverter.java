package fi.vm.sade.eperusteet.amosaa.service.mapping;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.OrganisaatioDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

@Component
public class OrganisaatioConverter extends BidirectionalConverter<OrganisaatioDto, String> {

    @Override
    public String convertTo(OrganisaatioDto source, Type<String> destinationType, MappingContext mappingContext) {
        return source.getOid();
    }

    @Override
    public OrganisaatioDto convertFrom(String source, Type<OrganisaatioDto> destinationType, MappingContext mappingContext) {
        OrganisaatioDto organisaatioDto = new OrganisaatioDto();
        organisaatioDto.setOid(source);


        return organisaatioDto;
    }
}
