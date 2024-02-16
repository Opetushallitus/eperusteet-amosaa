package fi.vm.sade.eperusteet.amosaa.service.mapping;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.LokalisoituTekstiRepository;

import java.util.Map;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LokalisoituTekstiConverter extends BidirectionalConverter<LokalisoituTeksti, LokalisoituTekstiDto> {

    @Autowired
    private LokalisoituTekstiRepository repository;

    @Override
    public LokalisoituTekstiDto convertTo(LokalisoituTeksti tekstiPalanen, Type<LokalisoituTekstiDto> type, MappingContext mappingContext) {
        return new LokalisoituTekstiDto(tekstiPalanen.getId(), tekstiPalanen.getTunniste(), tekstiPalanen.getTeksti());
    }

    @Override
    public LokalisoituTeksti convertFrom(LokalisoituTekstiDto dto, Type<LokalisoituTeksti> type, MappingContext mappingContext) {
        if (dto.getId() != null) {
            /*
            Jos id on mukana, yritä yhdistää olemassa olevaan tekstipalaseen
            Koska tekstipalanen on muuttumaton ja cachetettu, niin oletustapaus on että
            tekstipalanen on jo cachessa (luettu aikaisemmin) ja tietokantahaku vältetään.
            Huom! vihamielinen/virheellinen client voisi keksiä id:n aiheuttaen turhia tietokantahakuja.
            */
            LokalisoituTeksti current = repository.findOne(dto.getId());
            if (current != null) {
                Map<Kieli, String> teksti = current.getTeksti();
                teksti.putAll(dto.getTekstit());
                LokalisoituTeksti tekstiPalanen = LokalisoituTeksti.of(teksti, current.getTunniste());
                if (current.equals(tekstiPalanen)) {
                    return current;
                }
                return tekstiPalanen;
            }
        }
        return LokalisoituTeksti.of(dto.getTekstit());
    }
}
