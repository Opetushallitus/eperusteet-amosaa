package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaAikatauluDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaAikatauluService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelma/{opsId}/aikataulu")
@Api(value = "Aikataulut")
@InternalApi
public class OpetussuunnitelmaAikatauluController {

    @Autowired
    OpetussuunnitelmaAikatauluService opetussuunnitelmaAikatauluService;

    @RequestMapping(method = PUT)
    public ResponseEntity<List<OpetussuunnitelmaAikatauluDto>> updateOpetussuunnitelmanAikataulut(@PathVariable("ktId") Long ktId, @PathVariable("opsId") Long opsId, @RequestBody List<OpetussuunnitelmaAikatauluDto> aikataulut) {
        return ResponseEntity.ok(opetussuunnitelmaAikatauluService.save(ktId, opsId, aikataulut));
    }

    @RequestMapping(method = GET)
    public ResponseEntity<List<OpetussuunnitelmaAikatauluDto>> getOpetussuunnitelmanAikataulut(@PathVariable("ktId") Long ktId, @PathVariable("opsId") Long opsId) {
        return ResponseEntity.ok(opetussuunnitelmaAikatauluService.get(ktId, opsId));
    }
}
