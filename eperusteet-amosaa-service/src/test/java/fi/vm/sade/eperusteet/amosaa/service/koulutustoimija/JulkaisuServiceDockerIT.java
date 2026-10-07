package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.test.docker.AbstractDockerIntegrationTest;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testataan docker-tietokantaa vasten, johon ajetaan migraatiot.
 */
@DirtiesContext
@ActiveProfiles(profiles = {"docker"})
@Transactional
@SpringBootTest
public class JulkaisuServiceDockerIT extends AbstractDockerIntegrationTest {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @After
    public void clean() {
        arkistoiOpetussuunnitelmat();
    }

    @Test
    public void testJulkaise() {
        assertThat(julkaistujenMaara()).as("ei julkaisuja ennen julkaisua").isZero();

        startNewTransaction();
        OpetussuunnitelmaBaseDto ops1 = luoOpetussuunnitelma("ops-yksi");
        endTransaction();

        julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops1.getId(), julkaisuDto());
        assertThat(julkaisuService.viimeisinJulkaisuTila(getKoulutustoimijaId(), ops1.getId())).isEqualTo(JulkaisuTila.JULKAISTU);
        assertThat(julkaistujenMaara()).as("ensimmaisen julkaisun jalkeen").isEqualTo(1);
    }

    @Test
    public void testJulkaiseUudelleen() {
        startNewTransaction();
        OpetussuunnitelmaBaseDto ops = luoOpetussuunnitelma("ops-uudelleen");
        endTransaction();

        julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops.getId(), julkaisuDto());
        julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops.getId(), julkaisuDto());

        Opetussuunnitelma entity = opetussuunnitelmaRepository.findOne(ops.getId());
        assertThat(julkaisuRepository.findAllByOpetussuunnitelma(entity)).hasSize(2);
        assertThat(julkaistujenMaara()).as("uudelleenjulkaisu paivittaa yhden rivin").isEqualTo(1);
    }

    @Test
    public void testJulkaiseJaPoistaListauksesta() {
        startNewTransaction();
        OpetussuunnitelmaBaseDto ops1 = luoOpetussuunnitelma("ops-yksi");
        OpetussuunnitelmaBaseDto ops2 = luoOpetussuunnitelma("ops-kaksi");
        assertThat(julkaistujenMaara()).as("kaksi luonnos-opsia ei nay julkaistuissa").isZero();
        endTransaction();

        julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops1.getId(), julkaisuDto());
        julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops2.getId(), julkaisuDto());
        assertThat(julkaistujenMaara()).as("toisen julkaisun jalkeen").isEqualTo(2);

        opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), ops2.getId(), Tila.POISTETTU, false);
        assertThat(julkaistujenMaara()).as("poiston jalkeen").isEqualTo(1);
        assertThat(julkaistutIds()).containsExactly(ops1.getId());
    }

    private long julkaistujenMaara() {
        return julkaistut().getTotalElements();
    }

    private List<Long> julkaistutIds() {
        return julkaistut().getContent().stream()
                .map(OpetussuunnitelmaDto::getId)
                .toList();
    }

    private Page<OpetussuunnitelmaDto> julkaistut() {
        return julkaisuService.findOpetussuunnitelmatJulkaisut(new OpetussuunnitelmaJulkaistuQueryDto());
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
