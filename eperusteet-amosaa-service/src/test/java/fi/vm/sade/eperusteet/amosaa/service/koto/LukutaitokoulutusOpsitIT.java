package fi.vm.sade.eperusteet.amosaa.service.koto;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext
@Transactional
public class LukutaitokoulutusOpsitIT extends AbstractIntegrationTest {

    private static final Long LUKUTAITOKOULUTUS_PERUSTE_ID = 98860L;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Test
    public void testOpetussuunnitelmaCreate() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opetussuunnitelma -> opetussuunnitelma.setPerusteId(LUKUTAITOKOULUTUS_PERUSTE_ID));
        assertThat(ops.getPeruste().getKoulutustyyppi()).isEqualTo(KoulutusTyyppi.LUKUTAITOKOULUTUS);

        assertThat(perusteenTekstikappaleet(ops))
                .extracting(SisaltoViiteDto::getPerusteenOsaId)
                .containsExactlyInAnyOrder(98930L, 98931L);
        assertThat(sisaltoviitteet(ops).stream().filter(sv -> sv.getVanhempi() != null).collect(Collectors.toList()))
                .extracting(SisaltoViiteDto::getTyyppi)
                .containsOnly(SisaltoTyyppi.TEKSTIKAPPALE);
    }

    @Test
    public void testOpetussuunnitelmaToisestaCreate() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto pohja = createOpetussuunnitelma(opetussuunnitelma -> opetussuunnitelma.setPerusteId(LUKUTAITOKOULUTUS_PERUSTE_ID));
        SisaltoViiteDto pohjanKappale = perusteenTekstikappaleet(pohja).stream()
                .filter(tk -> tk.getPerusteenOsaId().equals(98930L))
                .findFirst().orElseThrow();
        pohjanKappale.getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("Pohjan paikallinen tarkennus"));
        sisaltoViiteService.updateSisaltoViite(pohja.getKoulutustoimija().getId(), pohja.getId(), pohjanKappale.getId(), pohjanKappale);

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opetussuunnitelma -> {
            opetussuunnitelma.setPerusteId(LUKUTAITOKOULUTUS_PERUSTE_ID);
            opetussuunnitelma.setOpsId(pohja.getId());
            opetussuunnitelma.setKoulutustyyppi(KoulutusTyyppi.LUKUTAITOKOULUTUS);
        });

        assertThat(ops.getId()).isNotEqualTo(pohja.getId());
        assertThat(ops.getPeruste().getKoulutustyyppi()).isEqualTo(KoulutusTyyppi.LUKUTAITOKOULUTUS);
        List<SisaltoViiteDto> tekstikappaleet = perusteenTekstikappaleet(ops);
        assertThat(tekstikappaleet).hasSize(2);
        SisaltoViiteDto kopioitu = tekstikappaleet.stream()
                .filter(tk -> tk.getPerusteenOsaId().equals(98930L))
                .findFirst().orElseThrow();
        assertThat(kopioitu.getTekstiKappale().getTeksti().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("Pohjan paikallinen tarkennus").getTekstit());
    }

    @Test
    public void testVainTekstikappaleitaSallitaan() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opetussuunnitelma -> opetussuunnitelma.setPerusteId(LUKUTAITOKOULUTUS_PERUSTE_ID));
        Long ktId = ops.getKoulutustoimija().getId();
        Long rootId = sisaltoviitteet(ops).stream()
                .filter(sv -> sv.getVanhempi() == null)
                .findFirst().orElseThrow()
                .getId();

        SisaltoViiteDto.Matala tekstikappale = new SisaltoViiteDto.Matala();
        tekstikappale.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        assertThat(sisaltoViiteService.addSisaltoViite(ktId, ops.getId(), rootId, tekstikappale)).isNotNull();

        SisaltoViiteDto.Matala opintokokonaisuus = new SisaltoViiteDto.Matala();
        opintokokonaisuus.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
        assertThatThrownBy(() -> sisaltoViiteService.addSisaltoViite(ktId, ops.getId(), rootId, opintokokonaisuus))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("ei-sallittu-sisaltoviite-tyyppi");
    }

    private List<SisaltoViiteDto> sisaltoviitteet(OpetussuunnitelmaBaseDto ops) {
        return sisaltoViiteService.getSisaltoViitteet(ops.getKoulutustoimija().getId(), ops.getId(), SisaltoViiteDto.class);
    }

    private List<SisaltoViiteDto> perusteenTekstikappaleet(OpetussuunnitelmaBaseDto ops) {
        return sisaltoviitteet(ops).stream()
                .filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null && viite.getPerusteenOsaId() != null)
                .collect(Collectors.toList());
    }
}
