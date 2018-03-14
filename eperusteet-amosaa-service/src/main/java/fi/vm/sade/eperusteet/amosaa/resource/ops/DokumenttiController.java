package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;

import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.DOKUMENTTI_KUVAN_LISAYS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.DOKUMENTTI_KUVAN_POISTO;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.DOKUMENTTI_PAIVITYS;

import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Api(value = "dokumentit")
@InternalApi
public class DokumenttiController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    DokumenttiService service;

    @Autowired
    OpetussuunnitelmaService opsService;

    @Autowired
    DokumenttiRepository repository;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) throws DokumenttiException {
        DokumenttiDto dto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpStatus status;

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (DokumenttiUtils.isTimePass(dto) || dto.getTila() != DokumenttiTila.LUODAAN) {
            dto = service.setStarted(ktId, opsId, dto);
            service.generateWithDto(ktId, opsId, dto);
            status = HttpStatus.ACCEPTED;
            return new ResponseEntity<>(dto, status);
        } else {
            throw new BusinessRuleViolationException("Luonti on jo käynissä");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> get(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<DokumenttiDto> update(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestBody DokumenttiDto body
    ) {
        DokumenttiDto newDto = service.update(ktId, opsId, Kieli.of(kieli), body);
        LogMessage.builder(OPETUSSUUNNITELMA, DOKUMENTTI_PAIVITYS).log();

        return Optional.ofNullable(newDto)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/kuva", method = RequestMethod.POST)
    public ResponseEntity<Object> addImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestPart MultipartFile file
    ) throws IOException {
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LogMessage.builder(OPETUSSUUNNITELMA, DOKUMENTTI_KUVAN_LISAYS).log();
        DokumenttiDto dto = service.addImage(ktId, opsId, dokumenttiDto, tyyppi, kieli, file);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @Transactional(readOnly = true)
    @RequestMapping(value = "/kuva", method = RequestMethod.GET)
    public ResponseEntity<Object> getImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Haetaan kuva
        // FIXME miksi tämä on kontrollerissa?
        List<Dokumentti> dokumentit = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));
        Dokumentti dokumentti = dokumentit.isEmpty() ? null : dokumentit.get(0);
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @Transactional
    @RequestMapping(value = "/kuva", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(ktId, opsId, Kieli.of(kieli));

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        // Haetaan kuva
        List<Dokumentti> dokumentit = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));
        Dokumentti dokumentti = dokumentit.isEmpty() ? null : dokumentit.get(0);
        if (dokumentti == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        LogMessage.builder(OPETUSSUUNNITELMA, DOKUMENTTI_KUVAN_POISTO).log();

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
