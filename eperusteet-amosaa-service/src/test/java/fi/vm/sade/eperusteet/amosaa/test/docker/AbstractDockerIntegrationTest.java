package fi.vm.sade.eperusteet.amosaa.test.docker;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.test.TestUser;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Julkaisutestejä varten tehty ja käyttää eri profiileja kuin muut testit.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles(profiles = {"docker"})
public abstract class AbstractDockerIntegrationTest {

    @Autowired
    protected KayttajanTietoService kayttajanTietoService;

    protected KoulutustoimijaBaseDto toimija;
    protected Kayttaja kayttaja;

    @Before
    public void setUpSecurityContext() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(TestUser.authenticated("kp2"));
        SecurityContextHolder.setContext(ctx);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        startNewTransaction();
        useProfileKP2();
        endTransaction();
    }

    protected Long getKoulutustoimijaId() {
        return toimija.getId();
    }

    protected void useProfileKP2() {
        updateProfile("kp2");
    }

    public void loginAsUser(String user) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(TestUser.authenticated(user));
        SecurityContextHolder.setContext(ctx);
    }

    public void invalidateAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private void updateProfile(String username) {
        loginAsUser(username);
        PermissionEvaluator.RolePrefix rolePrefix = PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA;
        kayttajanTietoService.updateKoulutustoimijat(rolePrefix);
        String userOrgOid = kayttajanTietoService.getUserOrganizations(rolePrefix).stream().findFirst().get();
        List<KoulutustoimijaBaseDto> koulutustoimijat = kayttajanTietoService.koulutustoimijat(rolePrefix);
        this.toimija = koulutustoimijat.stream()
                .filter(kt -> kt.getOrganisaatio().equals(userOrgOid))
                .findFirst()
                .get();
        kayttaja = kayttajanTietoService.getKayttaja();
    }

    public void startNewTransaction() {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }
        TestTransaction.start();
        TestTransaction.flagForCommit();
    }

    public void endTransaction() {
        TestTransaction.end();
    }
}
