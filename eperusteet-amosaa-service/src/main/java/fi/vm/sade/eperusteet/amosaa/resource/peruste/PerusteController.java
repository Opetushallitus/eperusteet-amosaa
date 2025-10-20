package fi.vm.sade.eperusteet.amosaa.resource.peruste;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaSuoritustapaDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/perusteet")
@Tag(name = "perusteet")
@InternalApi
public class PerusteController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService service;

    @Autowired
    private EperusteetClient eperusteet;

    @RequestMapping(value = "/haku", method = RequestMethod.GET)
    public JsonNode haePerusteista(@RequestParam  Map<String, String> params) {
        return eperusteet.findFromPerusteet(params);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PerusteDto getPeruste(@PathVariable Long id) {
        return service.getPerusteSisalto(id, PerusteDto.class);
    }

    @RequestMapping(value = "/{id}/kaikki", method = RequestMethod.GET)
    public JsonNode getPerusteAll(@PathVariable Long id) {
        return service.getPerusteSisalto(id, JsonNode.class);
    }

    @RequestMapping(value = "/{id}/tutkinnonosat", method = RequestMethod.GET)
    public JsonNode getPerusteTutkinnonOsat(@PathVariable Long id) {
        return service.getTutkinnonOsat(id);
    }

    @RequestMapping(value = "/{id}/suorituspolkuosat", method = RequestMethod.GET)
    public List<TutkinnonOsaSuoritustapaDto> getPerusteTutkinnonOsatKevyt(@PathVariable Long id) {
        List<TutkinnonOsaSuoritustapaDto> tosat = service.convertTutkinnonOsat(service.getTutkinnonOsat(id));
        return tosat;
    }

    @RequestMapping(value = "/{id}/tutkinnonosat/{tosaId}", method = RequestMethod.GET)
    public JsonNode getPerusteTutkinnonOsa(
            @PathVariable Long id,
            @PathVariable Long tosaId) {
        return service.getTutkinnonOsa(id, tosaId);
    }

    @RequestMapping(value = "/{id}/suoritustavat", method = RequestMethod.GET)
    public JsonNode getPerusteRakenteet(@PathVariable Long id) {
        return service.getSuoritustavat(id);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}", method = RequestMethod.GET)
    public JsonNode getPerusteRakenne(
            @PathVariable Long id,
            @PathVariable String st) {
        return service.getSuoritustapa(id, st);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}/tutkinnonosat/{tosaId}", method = RequestMethod.GET)
    public JsonNode getTutkinnonOsaViite(
            @PathVariable Long id,
            @PathVariable String st,
            @PathVariable Long tosaId
    ) {
        return service.getTutkinnonOsaViite(id, st, tosaId);
    }

    @RequestMapping(value = "/{id}/suoritustavat/{st}/tutkinnonosat", method = RequestMethod.GET)
    public JsonNode getTutkinnonOsaViitteet(
            @PathVariable Long id,
            @PathVariable String st
    ) {
        return service.getTutkinnonOsaViitteet(id, st);
    }

    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@RequestParam String diaarinumero) {
        return opetussuunnitelmaService.getPerusteenOpetussuunnitelmat(diaarinumero, OpetussuunnitelmaBaseDto.class);
    }

    @RequestMapping(value = "/{perusteId}/perusteesta", method = RequestMethod.GET)
    public JsonNode getPerusteByPerusteId(@PathVariable Long perusteId) {
        return service.getPerusteSisaltoByPerusteId(perusteId, JsonNode.class);
    }

    @RequestMapping(value = "/{perusteId}/perusteenosa/{perusteenosaId}", method = RequestMethod.GET)
    public PerusteenOsaDto getPerusteenOsa(@PathVariable Long perusteId, @PathVariable Long perusteenosaId) {
        return service.getPerusteenOsa(perusteId, perusteenosaId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/koulutustoimija/{ktId}/opetussuunnitelma/{opetussuunnitelmaId}/koulutuskoodillakorvaava", method = RequestMethod.GET)
    public PerusteDto getKoulutuskoodillaKorvaavaPeruste(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opetussuunnitelmaId
    ) {
        return service.getKoulutuskoodillaKorvaavaPeruste(ktId, opetussuunnitelmaId);
    }

    @GetMapping("/jaettujenosienpohjat")
    public List<PerusteKevytDto> getJaettujenOsienPohjat() {
        return eperusteet.getJaetunOsanPohjat();
    }

}
