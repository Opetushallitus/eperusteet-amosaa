package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiKuvaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiKuvaService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import io.swagger.annotations.Api;

import java.io.IOException;
import java.util.Optional;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Api(value = "dokumentit")
@InternalApi
public class DokumenttiController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private DtoMapper mapper;
    @Autowired
    DokumenttiService dokumenttiService;

    @Autowired
    DokumenttiKuvaService dokumenttiKuvaService;

    @Autowired
    OpetussuunnitelmaService opsService;

    @Autowired
    DokumenttiRepository repository;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> createDokumentti(@ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
                                                          @PathVariable Long opsId,
                                                          @RequestParam(defaultValue = "fi") String kieli) throws DokumenttiException {

        DokumenttiDto dto = dokumenttiService.createDtoFor(ktId, opsId, Kieli.of(kieli));

        if (dto != null && dto.getTila() != DokumenttiTila.EPAONNISTUI) {
            dokumenttiService.setStarted(ktId, opsId, dto);
            dokumenttiService.generateWithDto(ktId, opsId, dto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> getLatestDokumentti(@ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
                                                             @PathVariable("opsId") final Long opsId,
                                                             @RequestParam(defaultValue = "fi") final String kieli) {
        DokumenttiDto dto = dokumenttiService.getLatestDokumentti(ktId, opsId, Kieli.of(kieli));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> getLatestValmisDokumentti(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        DokumenttiDto dokumenttiDto = dokumenttiService.get(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] pdfdata = dokumenttiService.getDataByDokumenttiId(ktId, opsId, dokumenttiDto.getId());
        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        LokalisoituTekstiDto nimiDto = opsService.getOpetussuunnitelma(ktId, opsId).getNimi();
        String nimi = nimiDto.get(Kieli.of(kieli));
        if (nimi != null) {
            headers.set("Content-disposition", "inline; filename=\"" + nimi + ".pdf\"");
        } else {
            headers.set("Content-disposition", "inline; filename=\"" + dokumenttiDto.getId() + ".pdf\"");
        }

        headers.setContentLength(pdfdata.length);
        // estetään googlea indeksoimasta pdf:iä
        headers.set("X-Robots-Tag", "noindex");

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<DokumenttiDto> updateDokumentti(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestBody DokumenttiDto body
    ) {
        DokumenttiDto newDto = dokumenttiService.update(ktId, opsId, Kieli.of(kieli), body);

        return Optional.ofNullable(newDto)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> queryDokumenttiTila(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli) {

        DokumenttiDto dokumenttiDto = dokumenttiService.getValmisDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/dokumenttikuva", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiKuvaDto> getDokumenttiKuva(@ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
                                                               @PathVariable Long opsId,
                                                               @RequestParam(defaultValue = "fi") String kieli) {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.getDto(opsId, Kieli.of(kieli));

        if (dto == null) {
            dto = mapper.map(dokumenttiKuvaService.createDtoFor(opsId, Kieli.of(kieli)), DokumenttiKuvaDto.class);
        }
        return ResponseEntity.ok(dto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/kuva", method = RequestMethod.POST)
    public ResponseEntity<Object> addDokumenttiImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestPart MultipartFile file) throws IOException {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.addImage(opsId, tyyppi, Kieli.of(kieli), file);

        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")})
    @Transactional(readOnly = true)
    @RequestMapping(value = "/kuva", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDokumenttiImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli) {

        byte[] image = dokumenttiKuvaService.getImage(opsId, tyyppi, Kieli.of(kieli));
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @Transactional
    @RequestMapping(value = "/kuva", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDokumenttiImage(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli) {

        dokumenttiKuvaService.deleteImage(opsId, tyyppi, Kieli.of(kieli));
        return ResponseEntity.noContent().build();
    }
}
