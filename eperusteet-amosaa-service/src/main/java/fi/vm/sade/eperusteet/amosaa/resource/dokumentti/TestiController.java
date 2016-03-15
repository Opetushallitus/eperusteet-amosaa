package fi.vm.sade.eperusteet.amosaa.resource.dokumentti;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/testi")
public class TestiController {

    @RequestMapping(method = RequestMethod.GET)
    public String testi() {
        return "asd";
    }
}
