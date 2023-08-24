package fi.vm.sade.eperusteet.amosaa.service.koto;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KotoTaitotasoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KotoTaitotasoLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import fi.vm.sade.eperusteet.amosaa.test.AbstractH2IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class KotoOpsitIT extends AbstractH2IntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private SisaltoviiteServiceProvider sisaltoviiteServiceProvider;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Test
    public void testOpetussuunnitelmaCreate() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opetussuunnitelma -> opetussuunnitelma.setPerusteId(99860L));
        assertThat(ops.getPeruste().getKoulutustyyppi()).isEqualTo(KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS);
        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(ops.getKoulutustoimija().getId(), ops.getId(), SisaltoviiteLaajaDto.class);

        List<SisaltoviiteLaajaDto> tekstikappaleet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> kielitaitotasot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_KIELITAITOTASO.equals(viite.getTyyppi())).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> laot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_LAAJAALAINENOSAAMINEN.equals(viite.getTyyppi())).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> opinnot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_OPINTO.equals(viite.getTyyppi())).collect(Collectors.toList());
        assertThat(tekstikappaleet).hasSize(1);
        assertThat(kielitaitotasot).hasSize(1);
        assertThat(laot).hasSize(1);
        assertThat(opinnot).hasSize(1);

        assertThat(tekstikappaleet.get(0).getTekstiKappale().getNimi().getTekstit()).isEqualTo(LokalisoituTekstiDto.of("Perusteen tekstikappale").getTekstit());
        assertThat(tekstikappaleet.get(0).getPerusteenOsaId()).isEqualTo(99930l);
        assertThat(kielitaitotasot.get(0).getPerusteenOsaId()).isEqualTo(99931l);
        assertThat(laot.get(0).getPerusteenOsaId()).isEqualTo(99933l);
        assertThat(opinnot.get(0).getPerusteenOsaId()).isEqualTo(99932l);

    }

    @Test
    public void testOpetussuunnitelmaPohjastaCreate() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto pohja = createOpetussuunnitelma(opetussuunnitelma -> opetussuunnitelma.setPerusteId(99860L));
        {
            List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(pohja.getKoulutustoimija().getId(), pohja.getId(), SisaltoViiteDto.class);
            List<SisaltoViiteDto> tekstikappaleet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());
            List<SisaltoViiteDto> kielitaitotasot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_KIELITAITOTASO.equals(viite.getTyyppi())).collect(Collectors.toList());
            List<SisaltoViiteDto> laot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_LAAJAALAINENOSAAMINEN.equals(viite.getTyyppi())).collect(Collectors.toList());
            List<SisaltoViiteDto> opinnot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_OPINTO.equals(viite.getTyyppi())).collect(Collectors.toList());

            tekstikappaleet.get(0).getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("Pohjan paikallinen tarkennus"));
            sisaltoViiteService.updateSisaltoViite(pohja.getKoulutustoimija().getId(), pohja.getId(), tekstikappaleet.get(0).getId(), tekstikappaleet.get(0));

            laot.get(0).getKotoLaajaAlainenOsaaminen().setTeksti(LokalisoituTekstiDto.of("laoTeksti1"));
            sisaltoViiteService.updateSisaltoViite(pohja.getKoulutustoimija().getId(), pohja.getId(), laot.get(0).getId(), laot.get(0));

            kielitaitotasot.get(0).getKotoKielitaitotaso()
                    .setLaajaAlaisetOsaamiset(Arrays.asList(
                            KotoTaitotasoLaajaAlainenOsaaminenDto.builder()
                                    .koodiUri("laoUri2")
                                    .teksti(LokalisoituTekstiDto.of("laoTeksti2"))
                                    .build()
                    ));
            kielitaitotasot.get(0).getKotoKielitaitotaso()
                    .setTaitotasot(Arrays.asList(
                            KotoTaitotasoDto.builder()
                                    .koodiUri("taitotasoUri2")
                                    .sisaltoTarkennus(LokalisoituTekstiDto.of("sisaltotarkennus2"))
                                    .tavoiteTarkennus(LokalisoituTekstiDto.of("tavoitetarkennus2"))
                                    .build()
                    ));
            sisaltoViiteService.updateSisaltoViite(pohja.getKoulutustoimija().getId(), pohja.getId(), kielitaitotasot.get(0).getId(), kielitaitotasot.get(0));

            opinnot.get(0).getKotoOpinto()
                    .setLaajaAlaisetOsaamiset(Arrays.asList(
                            KotoTaitotasoLaajaAlainenOsaaminenDto.builder()
                                    .koodiUri("laoUri3")
                                    .teksti(LokalisoituTekstiDto.of("laoTeksti3"))
                                    .build()
                    ));
            opinnot.get(0).getKotoOpinto()
                    .setTaitotasot(Arrays.asList(
                            KotoTaitotasoDto.builder()
                                    .koodiUri("taitotasoUri3")
                                    .sisaltoTarkennus(LokalisoituTekstiDto.of("sisaltotarkennus3"))
                                    .tavoiteTarkennus(LokalisoituTekstiDto.of("tavoitetarkennus3"))
                                    .build()
                    ));
            sisaltoViiteService.updateSisaltoViite(pohja.getKoulutustoimija().getId(), pohja.getId(), opinnot.get(0).getId(), opinnot.get(0));
        }

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opetussuunnitelma -> {
            opetussuunnitelma.setPerusteId(99860L);
            opetussuunnitelma.setOpsId(pohja.getId());
        });
        List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(ops.getKoulutustoimija().getId(), ops.getId(), SisaltoViiteDto.class);
        List<SisaltoViiteDto> tekstikappaleet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());
        List<SisaltoViiteDto> kielitaitotasot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_KIELITAITOTASO.equals(viite.getTyyppi())).collect(Collectors.toList());
        List<SisaltoViiteDto> laot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_LAAJAALAINENOSAAMINEN.equals(viite.getTyyppi())).collect(Collectors.toList());
        List<SisaltoViiteDto> opinnot = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOTO_OPINTO.equals(viite.getTyyppi())).collect(Collectors.toList());

        assertThat(tekstikappaleet.get(0).getTekstiKappale().getTeksti().getTeksti()).isEqualTo(LokalisoituTekstiDto.of("Pohjan paikallinen tarkennus").getTekstit());
        assertThat(laot.get(0).getKotoLaajaAlainenOsaaminen().getTeksti().getTeksti()).isEqualTo(LokalisoituTekstiDto.of("laoTeksti1").getTekstit());

        assertThat(kielitaitotasot.get(0).getKotoKielitaitotaso().getLaajaAlaisetOsaamiset().get(0).getKoodiUri()).isEqualTo("laoUri2");
        assertThat(kielitaitotasot.get(0).getKotoKielitaitotaso().getLaajaAlaisetOsaamiset().get(0).getTeksti().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("laoTeksti2").getTekstit());
        assertThat(kielitaitotasot.get(0).getKotoKielitaitotaso().getTaitotasot().get(0).getKoodiUri()).isEqualTo("taitotasoUri2");
        assertThat(kielitaitotasot.get(0).getKotoKielitaitotaso().getTaitotasot().get(0).getTavoiteTarkennus().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("tavoitetarkennus2").getTekstit());
        assertThat(kielitaitotasot.get(0).getKotoKielitaitotaso().getTaitotasot().get(0).getSisaltoTarkennus().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("sisaltotarkennus2").getTekstit());

        assertThat(opinnot.get(0).getKotoOpinto().getLaajaAlaisetOsaamiset().get(0).getKoodiUri()).isEqualTo("laoUri3");
        assertThat(opinnot.get(0).getKotoOpinto().getLaajaAlaisetOsaamiset().get(0).getTeksti().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("laoTeksti3").getTekstit());
        assertThat(opinnot.get(0).getKotoOpinto().getTaitotasot().get(0).getKoodiUri()).isEqualTo("taitotasoUri3");
        assertThat(opinnot.get(0).getKotoOpinto().getTaitotasot().get(0).getTavoiteTarkennus().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("tavoitetarkennus3").getTekstit());
        assertThat(opinnot.get(0).getKotoOpinto().getTaitotasot().get(0).getSisaltoTarkennus().getTeksti())
                .isEqualTo(LokalisoituTekstiDto.of("sisaltotarkennus3").getTekstit());

    }
}
