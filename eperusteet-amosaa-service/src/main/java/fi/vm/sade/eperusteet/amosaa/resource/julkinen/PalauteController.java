package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.vm.sade.eperusteet.amosaa.dto.PalauteDto;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/palaute")
@Api(value = "Palautteet")
public class PalauteController {

    @Autowired
    private EperusteetClient eperusteetClient;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public PalauteDto sendPalaute(@RequestBody PalauteDto palauteDto) throws JsonProcessingException {
        return eperusteetClient.lahetaPalaute(palauteDto);
    }
    
}
