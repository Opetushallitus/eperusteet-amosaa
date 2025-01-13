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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Tag(name = "dokumentit")
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

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> createDokumentti(@Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
                                                          @PathVariable Long opsId,
                                                          @RequestParam String kieli) throws DokumenttiException {

        DokumenttiDto viimeisinJulkaistuDokumentti = dokumenttiService.getJulkaistuDokumentti(ktId, opsId, Kieli.of(kieli), null);
        if (viimeisinJulkaistuDokumentti != null && viimeisinJulkaistuDokumentti.getTila().equals(DokumenttiTila.EPAONNISTUI)) {
            dokumenttiService.setStarted(ktId, opsId, viimeisinJulkaistuDokumentti);
            dokumenttiService.generateWithDto(ktId, opsId, viimeisinJulkaistuDokumentti, opsService.getOpetussuunnitelmaJulkaistuSisalto(opsId));
        }

        DokumenttiDto dto = dokumenttiService.createDtoFor(ktId, opsId, Kieli.of(kieli));

        if (dto != null && dto.getTila() != DokumenttiTila.EPAONNISTUI) {
            dokumenttiService.setStarted(ktId, opsId, dto);
            dokumenttiService.generateWithDto(ktId, opsId, dto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> getLatestDokumentti(@Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
                                                             @PathVariable("opsId") final Long opsId,
                                                             @RequestParam final String kieli) {
        DokumenttiDto dto = dokumenttiService.getLatestDokumentti(ktId, opsId, Kieli.of(kieli));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> getLatestValmisDokumentti(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String kieli
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

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<DokumenttiDto> updateDokumentti(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @PathVariable Long id,
            @RequestParam String kieli,
            @RequestBody DokumenttiDto body
    ) {
        DokumenttiDto newDto = dokumenttiService.update(ktId, opsId, Kieli.of(kieli), body);

        return Optional.ofNullable(newDto)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> queryDokumenttiTila(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String kieli) {

        DokumenttiDto dokumenttiDto = dokumenttiService.getValmisDto(ktId, opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/dokumenttikuva", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiKuvaDto> getDokumenttiKuva(@Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
                                                               @PathVariable Long opsId,
                                                               @RequestParam String kieli) {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.getDto(opsId, Kieli.of(kieli));

        if (dto == null) {
            dto = mapper.map(dokumenttiKuvaService.createDtoFor(opsId, Kieli.of(kieli)), DokumenttiKuvaDto.class);
        }
        return ResponseEntity.ok(dto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/kuva", method = RequestMethod.POST)
    public ResponseEntity<Object> addDokumenttiImage(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam String kieli,
            @RequestPart MultipartFile file) throws IOException {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.addImage(opsId, tyyppi, Kieli.of(kieli), file);

        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Parameters({@Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)})
    @Transactional(readOnly = true)
    @RequestMapping(value = "/kuva", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDokumenttiImage(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam String kieli) {

        byte[] image = dokumenttiKuvaService.getImage(opsId, tyyppi, Kieli.of(kieli));
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @Transactional
    @RequestMapping(value = "/kuva", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDokumenttiImage(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String tyyppi,
            @RequestParam String kieli) {

        dokumenttiKuvaService.deleteImage(opsId, tyyppi, Kieli.of(kieli));
        return ResponseEntity.noContent().build();
    }
}
