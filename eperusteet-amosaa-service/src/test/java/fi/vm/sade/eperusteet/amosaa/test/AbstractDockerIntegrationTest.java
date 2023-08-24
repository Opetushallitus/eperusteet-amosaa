package fi.vm.sade.eperusteet.amosaa.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("/it-docker-test-context.xml")
@ActiveProfiles(profiles = {"docker"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractDockerIntegrationTest extends AbstractIntegrationTest {
}
