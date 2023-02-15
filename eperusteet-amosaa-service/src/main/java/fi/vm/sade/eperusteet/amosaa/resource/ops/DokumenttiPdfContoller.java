package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dokumentit")
@InternalApi
@Api(value = "Dokumentit")
public class DokumenttiPdfContoller {

    @Autowired
    DokumenttiService service;

    @PostMapping(path = "/pdf/data/{dokumenttiId}", consumes = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<String> savePdfData(@PathVariable("dokumenttiId") Long dokumenttiId,
                                              @RequestBody byte[] pdfData) {
        service.updateDokumenttiPdfData(pdfData, dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/pdf/tila/{dokumenttiId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> updateDokumenttiTila(@PathVariable("dokumenttiId") Long dokumenttiId,
                                                       @RequestBody DokumenttiTila tila) {
        service.updateDokumenttiTila(tila, dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
