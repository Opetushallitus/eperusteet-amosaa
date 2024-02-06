package fi.vm.sade.eperusteet.amosaa.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OrganisaatioService {

    @PreAuthorize("isAuthenticated()")
    JsonNode getOrganisaatio(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    OrganisaatioHierarkiaDto getOrganisaatioPuu(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    List<OrganisaatioHistoriaLiitosDto> getOrganisaationHistoriaLiitokset(String organisaatioOid);

    @PreAuthorize("permitAll()")
    LokalisoituTeksti haeOrganisaatioNimi(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaDto> getKoulutustoimijaOrganisaatiot();
}
