package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.*;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class SisaltoViiteServiceIT extends AbstractIntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private SisaltoViiteService service;

    @Autowired
    private DtoMapper mapper;

    @Test
    @Rollback
    public void testVierasOsaPerusteesta() {
        useProfileTest1();

        // Ops
        OpetussuunnitelmaDto ops = new OpetussuunnitelmaDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setPerusteDiaarinumero("OPH-12345-2018");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        HashMap<String, String> opsNimi = new HashMap<>();
        opsNimi.put("fi", "jaettu");
        ops.setNimi(new LokalisoituTekstiDto(opsNimi));

        // Sisältö viite
        SisaltoViiteDto.Matala viiteDto = new SisaltoViiteDto.Matala();
        viiteDto.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);

        TutkinnonosaDto tutkinnonosaDto = new TutkinnonosaDto();
        tutkinnonosaDto.setTyyppi(TutkinnonosaTyyppi.VIERAS);

        VierasTutkinnonosaDto vierasTutkinnonosaDto = new VierasTutkinnonosaDto();
        vierasTutkinnonosaDto.setPerusteId(3489211L);
        vierasTutkinnonosaDto.setTosaId(577941L);

        tutkinnonosaDto.setVierastutkinnonosa(vierasTutkinnonosaDto);

        viiteDto.setTosa(tutkinnonosaDto);

        TekstiKappaleDto tekstiKappaleDto = new TekstiKappaleDto();
        viiteDto.setTekstiKappale(tekstiKappaleDto);


        Opetussuunnitelma uusi = mapper.map(opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops), Opetussuunnitelma.class);
        Long ktId = getKoulutustoimijaId();
        Long opsId = uusi.getId();

        List<SisaltoViiteKevytDto> sisaltoViitteet = service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
        SisaltoViiteKevytDto tutkinnonOsat = sisaltoViitteet.stream()
                .filter(sv -> sv.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findAny().get();

        SisaltoViiteDto.Matala uusiViite = sisaltoViiteService.addSisaltoViite(ktId, opsId, tutkinnonOsat.getId(), viiteDto);
        assertThat(uusiViite).isNotNull();
    }
}
