package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.dto.pdf.PdfData;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/dokumentit")
@InternalApi
@Tag(name = "Dokumentit")
public class DokumenttiPdfContoller {

    @Autowired
    DokumenttiService dokumenttiService;

    @PostMapping(path = "/pdf/data/{dokumenttiId}")
    @ResponseBody
    public ResponseEntity<String> savePdfData(@PathVariable("dokumenttiId") Long dokumenttiId,
                                              @RequestBody PdfData pdfData) {
        dokumenttiService.updateDokumenttiPdfData(pdfData, dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/pdf/tila/{dokumenttiId}")
    @ResponseBody
    public ResponseEntity<String> updateDokumenttiTila(@PathVariable("dokumenttiId") Long dokumenttiId,
                                                       @RequestBody PdfData pdfData) {
        dokumenttiService.updateDokumenttiTila(DokumenttiTila.of(pdfData.getTila()), dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}