package fi.vm.sade.eperusteet.amosaa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioStatus;
import fi.vm.sade.eperusteet.amosaa.service.external.impl.OrganisaatioServiceImpl;
import fi.vm.sade.eperusteet.amosaa.service.external.impl.OrganisaatioServiceImpl.Client;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisaatioServiceImplTest {

    @Mock
    private Client client;

    @InjectMocks
    private OrganisaatioServiceImpl organisaatioService = new OrganisaatioServiceImpl();

    private final String OID = "1.2.4.5";
    private final String EMPTY_RETURN = "{\"childSuhteet\": [],\"parentSuhteet\": [],\"liitokset\": [],\"liittymiset\": []}";

    @Before
    public void setup() throws JsonProcessingException, FileNotFoundException, IOException {
        when(client.getOrganisaationHistoriaLiitokset(Mockito.anyString())).thenReturn(new ObjectMapper().readTree(loadOrganisaatioJson()));
    }

    @Test
    public void testGetOrganisaationHistoriaLiitokset() throws IOException {

        List<OrganisaatioHistoriaLiitosDto> liitokset = organisaatioService.getOrganisaationHistoriaLiitokset(OID);

        assertThat(liitokset).hasSize(2);
        assertThat(liitokset.get(0).getOrganisaatio()).isNotNull();
        assertThat(liitokset.get(0).getOrganisaatio().getOid()).isEqualTo("1.2.246.562.10.67185886982");
        assertThat(liitokset.get(0).getOrganisaatio().getNimi()).isEqualTo(Maps.newHashMap(Kieli.FI, "Keskuspuiston ammattiopisto"));
        assertThat(liitokset.get(0).getOrganisaatio().getStatus()).isEqualTo(OrganisaatioStatus.PASSIIVINEN);

    }

    @Test
    public void testGetOrganisaationHistoriaLiitokset_null() throws IOException {

        List<OrganisaatioHistoriaLiitosDto> liitokset = organisaatioService.getOrganisaationHistoriaLiitokset(SecurityUtil.OPH_OID);
        assertThat(liitokset).isNull();

        when(client.getOrganisaationHistoriaLiitokset(Mockito.anyString())).thenReturn(new ObjectMapper().readTree(EMPTY_RETURN));
        liitokset = organisaatioService.getOrganisaationHistoriaLiitokset(SecurityUtil.OPH_OID);
        assertThat(liitokset).isNull();
    }

    public File loadOrganisaatioJson() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:organisaatiohistoria.json");
    }

}
