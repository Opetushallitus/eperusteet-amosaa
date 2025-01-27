package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaMuokkaustietoDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelma/{opsId}/muokkaustieto")
@InternalApi
@Tag(name = "Muokkaustieto")
public class OpetussuunnitelmanMuokkaustietoController {

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService muokkausTietoService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<OpetussuunnitelmaMuokkaustietoDto>> getPerusteenMuokkausTiedotWithLuomisaika(@PathVariable("ktId") final Long ktId,
                                                                                                            @PathVariable("opsId") final Long opsId,
                                                                                                            @RequestParam(value = "viimeisinLuomisaika", required = false) final Long viimeisinLuomisaika,
                                                                                                            @RequestParam(value = "lukumaara", required = false, defaultValue = "10") int lukumaara) {
        return ResponseEntity.ok(muokkausTietoService.getOpetussuunnitelmanMuokkaustiedot(ktId, opsId, viimeisinLuomisaika != null ? new Date(viimeisinLuomisaika) : new Date(), lukumaara));
    }
}
