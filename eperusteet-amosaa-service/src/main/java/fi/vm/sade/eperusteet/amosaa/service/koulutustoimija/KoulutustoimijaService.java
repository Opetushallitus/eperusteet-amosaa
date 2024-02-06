package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

// TODO: Vaihda koulutustoimijakohtaiseen autentikaatioon
public interface KoulutustoimijaService {

    String OPH = "1.2.246.562.10.00000000001";

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> getKoulutustoimijat(Set<String> ktOid);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> getKoulutustoimijat();

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> initKoulutustoimijat(Set<String> ktOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KoulutustoimijaDto getKoulutustoimija(@P("ktId") Long ktId);

    @PreAuthorize("permitAll()")
    KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(@P("ktId") Long ktId);

    @PreAuthorize("permitAll()")
    KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(@P("ktId") String ktOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> List<T> getPaikallisetTutkinnonOsat(@P("ktId") Long ktId, Class<T> tyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    KoulutustoimijaDto updateKoulutustoimija(@P("ktId") Long ktId, KoulutustoimijaDto kt);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    void hylkaaYhteistyopyynto(@P("ktId") Long ktId, Long vierasKtId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KoulutustoimijaYstavaDto> getOrganisaatioHierarkiaYstavat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KoulutustoimijaYstavaDto> getOmatYstavat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KoulutustoimijaBaseDto> getPyynnot(@P("ktId") Long ktId);

    Long getKoulutustoimija(String idTaiOid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    OrganisaatioHierarkiaDto getOrganisaatioHierarkia(@P("ktId") Long ktId);

    @PreAuthorize("permitAll()")
    Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(PageRequest page, KoulutustoimijaQueryDto query);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OrganisaatioHistoriaLiitosDto> getOrganisaatioHierarkiaHistoriaLiitokset(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    EtusivuDto getEtusivu(@P("ktId") Long ktId, List<KoulutusTyyppi> koulutustyypit);

    @PreAuthorize("permitAll()")
    List<KoulutustoimijaJulkinenDto> findKoulutusatyypinKoulutustoimijat(Set<KoulutusTyyppi> koulutustyypit);

}
