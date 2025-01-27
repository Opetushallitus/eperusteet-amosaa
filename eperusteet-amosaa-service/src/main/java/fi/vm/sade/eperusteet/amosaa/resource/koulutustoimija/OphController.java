package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/opetussuunnitelmat")
@Tag(name = "opetussuunnitelmat")
@InternalApi
public class OphController {
    @Autowired
    private OpetussuunnitelmaService service;


    @RequestMapping(value = "/pohjat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getPohjat() {
        return service.getPohjat();
    }

    @RequestMapping(value = "/opspohjat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getOphOpsPohjat(@RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit) {
        return service.getOphOpsPohjat(koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()));
    }
}
