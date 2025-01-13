package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.tilastot.TilastotDto;
import fi.vm.sade.eperusteet.amosaa.dto.tilastot.ToimijaTilastotDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.tilastot.TilastotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tilastot")
@Tag(name = "tilastot")
@InternalApi
public class TilastotController {

    @Autowired
    TilastotService service;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.GET)
    TilastotDto get() {
        return service.getTilastot();
    }

    @RequestMapping(value = "/toimijat", method = RequestMethod.GET)
    ToimijaTilastotDto getTilastotToimijakohtaisesti() {
        return service.getTilastotToimijakohtaisesti();
    }

    @RequestMapping(value = "/opetussuunnitelmat/{sivu}/{sivukoko}", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastotSivu(@PathVariable("sivu") Integer sivu, @PathVariable("sivukoko") Integer sivukoko) {
        return opetussuunnitelmaService.getOpetussuunnitelmaTilastot(sivu, sivukoko);
    }
}
