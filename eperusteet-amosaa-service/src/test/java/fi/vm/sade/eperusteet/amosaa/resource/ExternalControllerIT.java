package fi.vm.sade.eperusteet.amosaa.resource;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.julkinen.ExternalController;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.test.docker.AbstractDockerIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles(profiles = {"docker"})
@Transactional
@SpringBootTest
public class ExternalControllerIT extends AbstractDockerIntegrationTest {

    private ExternalController externalController;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Before
    public void setUp() {
        externalController = new ExternalController();
        ReflectionTestUtils.setField(externalController, "julkaisuService", julkaisuService);
    }

    @After
    public void clean() {
        arkistoiOpetussuunnitelmat();
    }

    @Test
    public void testOpetussuunnitelmaViimeisinRevisio() {
        OpetussuunnitelmaBaseDto ops = luoJaJulkaise("ops-viimeisin", 2);

        OpetussuunnitelmaKaikkiDto julkaistu = externalController.getPublicOpetussuunnitelma(ops.getId());
        assertThat(julkaistu).isNotNull();
        assertThat(julkaistu.getId()).isEqualTo(ops.getId());
        assertThat(julkaistu.getJulkaisuRevisio()).isEqualTo(viimeisinRevisio(ops.getId()));
    }

    @Test
    public void testOpetussuunnitelmaRevisioLoytyy() {
        OpetussuunnitelmaBaseDto ops = luoJaJulkaise("ops-revisio", 2);

        List<Integer> revisiot = revisiot(ops.getId());
        assertThat(revisiot).hasSize(2);
        revisiot.forEach(revisio -> {
            ResponseEntity<OpetussuunnitelmaKaikkiDto> response = externalController.getPublicOpetussuunnitelmaJulkaisuRevisio(ops.getId(), revisio);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(ops.getId());
            assertThat(response.getBody().getJulkaisuRevisio()).isEqualTo(revisio);
        });
    }

    @Test
    public void testOpetussuunnitelmaRevisioEiLoydy() {
        OpetussuunnitelmaBaseDto ops = luoJaJulkaise("ops-ei-loydy", 1);

        ResponseEntity<OpetussuunnitelmaKaikkiDto> response = externalController.getPublicOpetussuunnitelmaJulkaisuRevisio(ops.getId(), 999);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void testOpetussuunnitelmaRevisioReititys() throws Exception {
        OpetussuunnitelmaBaseDto ops = luoJaJulkaise("ops-reititys", 1);
        int revisio = viimeisinRevisio(ops.getId());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(externalController).build();

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/v" + revisio))
                .andExpect(MockMvcResultMatchers.handler().methodName("getPublicOpetussuunnitelmaJulkaisuRevisio"));

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/v999"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getPublicOpetussuunnitelmaJulkaisuRevisio"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/id"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getOpetussuunnitelmaDynamicQuery"));

        mvc.perform(MockMvcRequestBuilders.get("/api/external/opetussuunnitelma/" + ops.getId() + "/nimi/fi"))
                .andExpect(MockMvcResultMatchers.handler().methodName("getOpetussuunnitelmaDynamicQueryHidden"));
    }

    private OpetussuunnitelmaBaseDto luoJaJulkaise(String nimi, int julkaisuja) {
        startNewTransaction();
        OpetussuunnitelmaBaseDto ops = luoOpetussuunnitelma(nimi);
        endTransaction();

        for (int i = 0; i < julkaisuja; i++) {
            julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops.getId(), julkaisuDto());
        }
        return ops;
    }

    private List<Integer> revisiot(Long opsId) {
        startNewTransaction();
        List<Integer> revisiot = julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelmaRepository.findOne(opsId)).stream()
                .map(Julkaisu::getRevision)
                .toList();
        endTransaction();
        return revisiot;
    }

    private int viimeisinRevisio(Long opsId) {
        return revisiot(opsId).stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElseThrow();
    }

    private OpetussuunnitelmaBaseDto luoOpetussuunnitelma(String nimi) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(toimija);
        ops.setPerusteDiaarinumero("9/011/2008");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        ops.setJulkaisukielet(new HashSet<>(Set.of(Kieli.FI)));
        HashMap<String, String> nimet = new HashMap<>();
        nimet.put("fi", nimi);
        ops.setNimi(new LokalisoituTekstiDto(nimet));
        ops.setPerusteId(515491L);
        return opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
    }

    private JulkaisuBaseDto julkaisuDto() {
        return JulkaisuBaseDto.builder()
                .tiedote(LokalisoituTekstiDto.of("julkaisutiedote"))
                .build();
    }

    private void arkistoiOpetussuunnitelmat() {
        opetussuunnitelmaRepository.findAll().forEach(ops -> {
            if (ops.getTila() != Tila.POISTETTU) {
                opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), ops.getId(), Tila.POISTETTU, false);
            }
        });
    }
}
