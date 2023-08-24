package fi.vm.sade.eperusteet.amosaa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.test.AbstractH2IntegrationTest;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class MappingIT extends AbstractH2IntegrationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testOrganisaatioMapping() throws IOException {
        OrganisaatioHierarkiaDto org = OrganisaatioHierarkiaDto.builder()
                .oid("1")
                .children(Collections.singletonList(OrganisaatioHierarkiaDto.builder()
                        .oid("2")
                        .build()))
                .build();
        String val = mapper.writeValueAsString(org);
        JsonNode node = mapper.readTree(val);
        assertThat(node.has("all")).isFalse();
    }

    @Test
    public void testAuditLoggerMapping() throws IOException {
        String val = mapper.writeValueAsString(new PoistettuDto());
        JsonNode node = mapper.readTree(val);
        assertThat(node.has("auditLogger")).isFalse();
    }

}
