package fi.vm.sade.eperusteet.amosaa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliTunnisteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuDto;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;

@DirtiesContext
@Transactional
public class EperusteetServiceIT extends AbstractIntegrationTest {

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Test
    public void getSuoritustavatTest() {

        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());

        sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getLapset().get(0).getIdLong(),
                createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.SUORITUSPOLKU);
                    sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of(UUID.fromString("d35fb695-f181-4e49-b4b9-c64a85819d0a"), false, LokalisoituTekstiDto.of("testi1"), Sets.newHashSet("koodi1"))); // 1
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("0b9aad4c-8025-4149-8e73-27394a5adc50", true, null)); // 1-1
                                                                                                               // --hidden
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of(UUID.fromString("2f91a972-c52f-4db4-83be-3e16d668cb46"), false, LokalisoituTekstiDto.of("testi2"), Sets.newHashSet("koodi2"))); // 1-2
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("76044b5b-1f4b-4587-b30d-23b7d3c2608d", true, null)); // 2
                                                                                                               // --hidden
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("c4a3c67e-4f1e-4db8-8ee8-a8dc03d7749f", false, null)); // 2-1
                                                                                                                // --hidden
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("e274c79e-8d39-45e1-ace0-1cd2a4127f29", false, null)); // 2-2
                                                                                                                // --hidden
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of(UUID.fromString("428e7f22-0a69-43c5-baa5-520296f71169"), false, LokalisoituTekstiDto.of("testi3"), Sets.newHashSet("koodi3"))); // 3
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("6e17d79d-6da9-41c6-8237-9facceba24ad", true, null)); // 3-1
                                                                                                               // --hidden
                }));

        List<RakenneModuuliTunnisteDto> suoritustavat = eperusteetService.getSuoritustavat(getKoulutustoimijaId(), ops.getId());

        assertThat(suoritustavat).hasSize(1);

        RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto1 = (RakenneModuuliTunnisteDto) suoritustavat.get(0).getOsat().get(0);
        assertThat(rakenneModuuliTunnisteDto1).extracting("tunniste").isEqualTo(UUID.fromString("d35fb695-f181-4e49-b4b9-c64a85819d0a"));
        assertThat(rakenneModuuliTunnisteDto1.getKoodit()).isEqualTo(Sets.newHashSet("koodi1"));
        assertThat(rakenneModuuliTunnisteDto1.getKuvaus().getTeksti().get(Kieli.FI)).isEqualTo("testi1");

        assertThat(rakenneModuuliTunnisteDto1.getOsat().get(0)) .extracting("tunniste").isEqualTo(UUID.fromString("2f91a972-c52f-4db4-83be-3e16d668cb46"));

        RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto1_1 = (RakenneModuuliTunnisteDto)((RakenneModuuliTunnisteDto)suoritustavat.get(0).getOsat().get(0)).getOsat().get(0);
        assertThat(rakenneModuuliTunnisteDto1_1.getKoodit()).isEqualTo(Sets.newHashSet("koodi2"));
        assertThat(rakenneModuuliTunnisteDto1_1.getKuvaus().getTeksti().get(Kieli.FI)).isEqualTo("testi2");

        RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto2 = (RakenneModuuliTunnisteDto) suoritustavat.get(0).getOsat().get(1);
        assertThat(rakenneModuuliTunnisteDto2).extracting("tunniste").isEqualTo(UUID.fromString("428e7f22-0a69-43c5-baa5-520296f71169"));
        assertThat(rakenneModuuliTunnisteDto2.getKoodit()).isEqualTo(Sets.newHashSet("koodi3"));
        assertThat(rakenneModuuliTunnisteDto2.getKuvaus().getTeksti().get(Kieli.FI)).isEqualTo("testi3");

    }

}
