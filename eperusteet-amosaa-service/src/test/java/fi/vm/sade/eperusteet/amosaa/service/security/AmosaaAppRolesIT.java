package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractH2IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DirtiesContext
@Transactional
public class AmosaaAppRolesIT extends AbstractH2IntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Test
    public void tuvaOikeudetTest() {
        useProfileTuva();
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(76091l));
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();

        useProfileKP1();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileVst();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileKoto();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileOPH();
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();
    }

    @Test
    public void vstOikeudetTest() {
        useProfileVst();
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();

        useProfileKP1();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileTuva();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileKoto();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileOPH();
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();
    }

    @Test
    public void kotoOikeudetTest() {
        useProfileKoto();
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(99860l));
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();

        useProfileKP1();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileVst();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileTuva();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileOPH();
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();
    }

    @Test
    public void amosaaOikeudetTest() {
        useProfileKP1();
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(515491l));
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();

        useProfileVst();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileKoto();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileTuva();
        assertThatThrownBy(() -> sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class))
                .isInstanceOf(AccessDeniedException.class);

        useProfileOPH();
        assertThat(sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class)).isNotNull();
    }
}
