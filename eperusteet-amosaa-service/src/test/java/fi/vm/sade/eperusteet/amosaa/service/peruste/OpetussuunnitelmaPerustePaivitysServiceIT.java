package fi.vm.sade.eperusteet.amosaa.service.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.EperusteetClientMock;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class OpetussuunnitelmaPerustePaivitysServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Before
    public void setup(){
        useProfileVst();
    }

    @Test
    public void opetussuunnitelmaPerusteSisaltoLisaysPoistoTest() {
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(9711L));
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(vstOps.getId());

        assertThat(opetussuunnitelma.getSisaltoviitteet()).hasSize(1);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset()).hasSize(2);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getTekstiKappale().getNimi().getTeksti().get(Kieli.FI)).isEqualTo("tk1");
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset()).hasSize(1);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(0).getLapset()).hasSize(1);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(0).getLapset().get(0).getLapset()).hasSize(0);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(1).getLapset()).hasSize(0);

        testSisaltoMaara(opetussuunnitelma, SisaltoTyyppi.TEKSTIKAPPALE, 3, true);
        testSisaltoMaara(opetussuunnitelma, SisaltoTyyppi.OPINTOKOKONAISUUS, 1, true);

        createOpintokokonaisuus(vstOps.getId(), opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(0).getLapset().get(0).getId(), "oma1");
        createOpintokokonaisuus(vstOps.getId(), opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(0).getId(), "oma2");

        paivitaOpsCachedPeruste(opetussuunnitelma);
        opetussuunnitelmaService.paivitaPeruste(getKoulutustoimijaId(), opetussuunnitelma.getId());

        opetussuunnitelma = opetussuunnitelmaRepository.findOne(vstOps.getId());
        testSisaltoMaara(opetussuunnitelma, SisaltoTyyppi.TEKSTIKAPPALE, 2, true);
        testSisaltoMaara(opetussuunnitelma, SisaltoTyyppi.TEKSTIKAPPALE, 2, false);
        testSisaltoMaara(opetussuunnitelma, SisaltoTyyppi.OPINTOKOKONAISUUS, 3, true);

        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getTekstiKappale().getNimi().getTeksti().get(Kieli.FI)).isEqualTo("tk1");
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(1).getTekstiKappale().getNimi().getTeksti().get(Kieli.FI)).isEqualTo("tk2");

        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getPerusteenOsaId()).isNotNull();
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset()).hasSize(2);
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(0).getPerusteenOsaId()).isNull();
        assertThat(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset().get(0).getLapset().get(1).getPerusteenOsaId()).isNull();
    }

    private void paivitaOpsCachedPeruste(Opetussuunnitelma opetussuunnitelma) {
        CachedPeruste cachedPeruste = cachedPerusteRepository.findOne(opetussuunnitelma.getPeruste().getId());
        cachedPeruste.setPerusteId(9712L);
        cachedPerusteRepository.save(cachedPeruste);
    }

    private void testSisaltoMaara(Opetussuunnitelma opetussuunnitelma, SisaltoTyyppi tyyppi, int lkm, boolean perusteesta) {
        assertThat(CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet().get(0).getLapset(), SisaltoViite::getLapset)
                .filter(sv -> sv.getTyyppi().equals(tyyppi)
                        && ((!perusteesta && sv.getPerusteenOsaId() == null)
                            || (perusteesta && sv.getPerusteenOsaId() != null))).collect(Collectors.toList())).hasSize(lkm);
    }

    private SisaltoViiteDto.Matala createOpintokokonaisuus(Long opsId, Long rootId, String nimi) {
        return sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), opsId, rootId,
                createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
                    sisaltoViiteDto.setTekstiKappale(new TekstiKappaleDto(LokalisoituTekstiDto.of(nimi), null, null));
                }));
    }
}
