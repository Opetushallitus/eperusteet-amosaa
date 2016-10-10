package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Api(value = "dokumentit")
public class DokumenttiController {

    @Autowired
    DokumenttiService service;

    @Autowired
    OpetussuunnitelmaService opsService;

    @Autowired
    DokumenttiRepository repository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(
            @PathVariable Long ktId,
            @PathVariable Long opsId,
            @RequestParam(value = "kieli", defaultValue = "fi") String kieli) throws DokumenttiException {
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpStatus status = HttpStatus.ACCEPTED;
        int maxTimeInMinutes = 2;

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (dokumenttiDto.getAloitusaika() == null
                || DokumenttiUtils.isTimePass(dokumenttiDto)
                || dokumenttiDto.getTila() != DokumenttiTila.LUODAAN) {
            // Vaihdetaan dokumentin tila luonniksi
            service.setStarted(ktId, opsId, dokumenttiDto);

            // Generoidaan dokumentin datasisältö
            // Asynkroninen metodi
            service.generateWithDto(ktId, opsId, dokumenttiDto);
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(dokumenttiDto, status);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> get(@PathVariable Long ktId,
                                      @PathVariable Long opsId,
                                      @RequestParam(defaultValue = "fi") String kieli) {
        byte[] pdfdata = service.get(ktId, opsId, Kieli.of(kieli));

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        LokalisoituTekstiDto nimiDto = opsService.getOpetussuunnitelma(ktId, opsId).getNimi();
        String nimi = nimiDto.get(Kieli.of(kieli));
        if (nimi != null) {
            headers.set("Content-disposition", "inline; filename=\"" + nimi + ".pdf\"");
        } else {
            DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
            headers.set("Content-disposition", "inline; filename=\"" + dokumenttiDto.getId() + ".pdf\"");
        }

        headers.setContentLength(pdfdata.length);

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(@PathVariable Long ktId,
                                                @PathVariable Long opsId,
                                                @RequestParam(value = "kieli", defaultValue = "fi") String kieli) {

        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            dokumenttiDto = service.createDtoFor(ktId, opsId, Kieli.of(kieli));
        }

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @RequestMapping(value = "/kuva", method=RequestMethod.POST)
    public ResponseEntity<Object> addImage(@PathVariable Long ktId,
                                            @PathVariable Long opsId,
                                            @RequestParam String tyyppi,
                                            @RequestParam(defaultValue = "fi") String kieli,
                                            @RequestPart MultipartFile file) throws IOException {
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DokumenttiDto dto = service.addImage(ktId, opsId, dokumenttiDto, tyyppi, kieli, file);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/kuva", method=RequestMethod.GET)
    public ResponseEntity<Object> getImage(@PathVariable Long ktId,
                                            @PathVariable Long opsId,
                                            @RequestParam String tyyppi,
                                            @RequestParam(defaultValue = "fi") String kieli) {
        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Haetaan kuva
        Dokumentti dokumentti = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));
        if (dokumentti == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] image;

        switch (tyyppi) {
            case "kansikuva":
                image = dokumentti.getKansikuva();
                break;
            case "ylatunniste":
                image = dokumentti.getYlatunniste();
                break;
            case "alatunniste":
                image = dokumentti.getAlatunniste();
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (image == null || image.length == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/kuva", method=RequestMethod.DELETE)
    public ResponseEntity<Object> deleteImage(@PathVariable Long ktId,
                                           @PathVariable Long opsId,
                                           @RequestParam(required = true) String tyyppi,
                                           @RequestParam(defaultValue = "fi") String kieli) {

        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        // Haetaan kuva
        Dokumentti dokumentti = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));
        if (dokumentti == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        switch (tyyppi) {
            case "kansikuva":
                dokumentti.setKansikuva(null);
                break;
            case "ylatunniste":
                dokumentti.setYlatunniste(null);
                break;
            case "alatunniste":
                dokumentti.setAlatunniste(null);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        repository.save(dokumentti);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
