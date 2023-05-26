package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/julkinen")
@Api(value = "julkinen")
public class JulkinenController {

    @Autowired
    private KoulutustoimijaService ktService;

    @Autowired
    private SisaltoViiteService svService;

    @Autowired
    private OpetussuunnitelmaService opsService;

    @Autowired
    private TiedoteService tiedoteService;

    @Autowired
    DokumenttiService dokumenttiService;

    @RequestMapping(value = "/tutkinnonosat/{koodi}", method = RequestMethod.GET)
    @ApiIgnore
    public SisaltoViiteDto getTutkinnonOsa(
            @PathVariable final String koodi
    ) {
        throw new UnsupportedOperationException("ei-toteutettu-viela");
    }

    @RequestMapping(value = "/opetussuunnitelmat/{opsId}/koulutustoimija", method = RequestMethod.GET)
    @Description("Opetussuunnitelman omistavan toimijan tiedot.")
    public KoulutustoimijaJulkinenDto getOpetussuunnitelmanToimija(
            @PathVariable final Long opsId
    ) {
        return opsService.getKoulutustoimijaId(opsId);
    }

    @RequestMapping(value = "/koulutustoimijat/org/{ktOid}", method = RequestMethod.GET)
    @Description("Koulutuksen järjestäjän tiedot organisaation oidin perusteella.")
    public KoulutustoimijaJulkinenDto getKoulutustoimijaByOid(
            @PathVariable final String ktOid
    ) {
        return ktService.getKoulutustoimijaJulkinen(ktOid);
    }

    @RequestMapping(value = "/koulutustoimijat/{ktId}", method = RequestMethod.GET)
    @Description("Koulutuksen järjestäjän tiedot.")
    public KoulutustoimijaJulkinenDto getKoulutustoimijaByKtId(
            @PathVariable final Long ktId
    ) {
        return ktService.getKoulutustoimijaJulkinen(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "organisaatioRyhma", dataType = "boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "oppilaitosTyyppiKoodiUri", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = true),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien parametrihaku.")
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(
            @ApiIgnore final OpetussuunnitelmaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        return opsService.findOpetussuunnitelmat(p, pquery);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "organisaatioRyhma", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "oppilaitosTyyppiKoodiUri", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "tuleva", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "voimassaolo", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "poistunut", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "jotpatyyppi", dataType = "string", paramType = "query", allowMultiple = true),
    })
    @RequestMapping(value = "/opetussuunnitelmat/julkaisut", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien parametrihaku.")
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmatJulkaisut(
            @ApiIgnore final OpetussuunnitelmaJulkaistuQueryDto pquery
    ) {
        return opsService.findOpetussuunnitelmatJulkaisut(pquery);
    }

    @RequestMapping(value = "/perusteenopetussuunnitelmat", method = RequestMethod.GET)
    @Description("")
    public List<OpetussuunnitelmaJulkinenDto> getPerusteenOpetussuunnitelmat(
            @RequestParam final String perusteenDiaarinumero
    ) {
        return opsService.getJulkaistutPerusteenOpetussuunnitelmat(perusteenDiaarinumero, OpetussuunnitelmaJulkinenDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "organisaatioRyhma", dataType = "boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = true),
    })
    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(
            @ApiIgnore final KoulutustoimijaQueryDto pquery
    ) {
        // Oletuksena älä palauta pohjia
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 1000));
        return ktService.findKoulutustoimijat(p, pquery);
    }

    @RequestMapping(value = "/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getSisaltoViiteByKoodi(
            @PathVariable final String koodi
    ) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(null, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getByKoodi(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final String koodi
    ) {
        return ResponseEntity.ok(svService.getByKoodiJulkinen(ktId, koodi, SisaltoViiteSijaintiDto.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOpetussuunnitelmat(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return opsService.getJulkisetOpetussuunnitelmat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/tiedotteet", method = RequestMethod.GET)
    public List<TiedoteDto> getJulkisetTiedotteet(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId
    ) {
        return tiedoteService.getJulkisetTiedotteet(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto getOpetussuunnitelmaJulkinen(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelma(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti", method = RequestMethod.GET)
    public ResponseEntity<Object> getDokumentti(@ApiIgnore @ModelAttribute("ktId") final Long ktId,
                                                @PathVariable final Long opsId,
                                                @RequestParam(defaultValue = "fi") final String kieli,
                                                @RequestParam final Long dokumenttiId) {
        byte[] pdfdata = dokumenttiService.getDataByDokumenttiId(ktId, opsId, dokumenttiId);

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        LokalisoituTekstiDto nimiDto = opsService.getOpetussuunnitelma(ktId, opsId).getNimi();
        String nimi = nimiDto.get(Kieli.of(kieli));
        if (nimi != null) {
            headers.set("Content-disposition", "inline; filename=\"" + nimi + ".pdf\"");
        } else {
            headers.set("Content-disposition", "inline; filename=\"" + dokumenttiId + ".pdf\"");
        }

        headers.setContentLength(pdfdata.length);
        // estetään googlea indeksoimasta pdf:iä
        headers.set("X-Robots-Tag", "noindex");

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> queryDokumentti(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        DokumenttiDto dokumenttiDto = null;
        try {
            dokumenttiDto = dokumenttiService.getValmisDto(ktId, opsId, Kieli.of(kieli));
        } catch (AccessDeniedException e) {
            dokumenttiDto = new DokumenttiDto();
        }

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti/julkaistu", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> getJulkaistuDokumentti(@ApiIgnore @ModelAttribute("ktId") final Long ktId,
                                                         @PathVariable Long opsId,
                                                         @RequestParam(defaultValue = "fi") String kieli,
                                                         @RequestParam(required = false) Integer revision) {

        return ResponseEntity.ok(dokumenttiService.getJulkaistuDokumentti(ktId, opsId, Kieli.of(kieli), revision));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kaikki", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelmaKaikki(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/julkaisu", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestParam(value = "esikatselu", required = false) final boolean esikatselu
    ) {
        return opsService.getOpetussuunnitelmaJulkaistuSisalto(ktId, opsId, esikatselu);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/otsikot", method = RequestMethod.GET)
    public List<SisaltoViiteKevytDto> getOpetussuunnitelmaOtsikot(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return svService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{svId}", method = RequestMethod.GET)
    public SisaltoViiteDto.Matala getOpetussuunnitelmaTekstit(
            @ApiIgnore @ModelAttribute("ktId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return svService.getSisaltoViite(ktId, opsId, svId);
    }

    @RequestMapping(value = "/koulutustoimijat/koulutystyypilla", method = RequestMethod.GET)
    public List<KoulutustoimijaJulkinenDto> findKoulutusatyypinKoulutustoimijat(
            @RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit
    ) {
        return ktService.findKoulutusatyypinKoulutustoimijat(koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()));
    }
}
