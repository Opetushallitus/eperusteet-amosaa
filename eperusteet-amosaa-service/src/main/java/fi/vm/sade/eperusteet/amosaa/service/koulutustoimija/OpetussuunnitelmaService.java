package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuInternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaListausDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface OpetussuunnitelmaService extends RevisionService {

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaBaseDto> getPohjat();

    @PreAuthorize("isAuthenticated()")
    void mapPerusteIds();

    @PreAuthorize("isAuthenticated()")
    void mapKoulutustyyppi();

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaDto> getJulkisetOpetussuunnitelmat(Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    Page<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@P("ktId") Long ktId, PageRequest page, OpsHakuDto query);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> Page<T> getOpetussuunnitelmat(@P("ktId") Long ktId, PageRequest page, OpsHakuDto query, Class<T> clazz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS')")
    KoulutustoimijaJulkinenDto getKoulutustoimijaId(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaDto getOpetussuunnitelma(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    <T extends OpetussuunnitelmaBaseDto> T getOpetussuunnitelma(Long ktId, Long opsId, Class<T> type);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaBaseDto getOpetussuunnitelmaPohjaKevyt(Long ktId, Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuDto> getPoistetut(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<KayttajaoikeusDto> getOikeudet(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    KayttajaoikeusDto updateOikeus(@P("ktId") Long ktId, @P("opsId") Long opsId, Long oikeusId, KayttajaoikeusDto oikeus);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto update(@P("ktId") Long ktId, @P("opsId") Long opsId, OpetussuunnitelmaDto body);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto revertTo(@P("ktId") Long ktId, @P("opsId") Long opsId, Integer revId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    void paivitaPeruste(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    List<VanhentunutPohjaperusteDto> haePaivitystaVaativatPerusteet(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    VanhentunutPohjaperusteDto haePaivitystaVaativaPeruste(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUONTI')")
    OpetussuunnitelmaBaseDto addOpetussuunnitelma(@P("ktId") Long ktId, OpetussuunnitelmaLuontiDto opsDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA') or hasPermission(null, 'oph', 'HALLINTA')")
    OpetussuunnitelmaBaseDto updateTila(@P("ktId") Long ktId, @P("opsId") Long opsId, Tila tila, boolean generatePdf);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<Validointi> validoi(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOtherOpetussuunnitelmat(@P("ktId") Long ktId, Set<KoulutusTyyppi> koulutustyypit);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    JsonNode getOpetussuunnitelmanPeruste(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("permitAll()")
    <T extends OpetussuunnitelmaBaseDto> List<T> getPerusteenOpetussuunnitelmat(String diaari, Class<T> type);

    @PreAuthorize("permitAll()")
    <T extends OpetussuunnitelmaBaseDto> List<T> getJulkaistutPerusteenOpetussuunnitelmat(String diaari, Class<T> type);

    @PreAuthorize("permitAll()")
    Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(PageRequest p, OpetussuunnitelmaQueryDto pquery);

    @PreAuthorize("permitAll()")
    Page<OpetussuunnitelmaDto> findOpetussuunnitelmatJulkaisut(OpetussuunnitelmaJulkaistuQueryDto pquery);

    @PreAuthorize("permitAll()")
    SisaltoviiteOpintokokonaisuusExternalDto findJulkaistuOpintokokonaisuus(String koodiArvo) throws IOException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaDto updateKoulutustoimija(@P("ktId") Long ktId, @P("opsId") Long opsId, KoulutustoimijaBaseDto body);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    OpetussuunnitelmaDto updateKoulutustoimijaPassivoidusta(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("permitAll()")
    OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(@P("ktId") Long ktId, @P("opsId") Long opsId, boolean esikatselu);

    @PreAuthorize("hasPermission(null, 'OPH','HALLINTA')")
    void updateOpetussuunnitelmaSisaltoviitePiilotukset();

    @PreAuthorize("isAuthenticated()")
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(String organisaatioId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    NavigationNodeDto buildNavigation(Long ktId, Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    @Deprecated
    NavigationNodeDto buildNavigationJulkinen(Long ktId, Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    NavigationNodeDto buildNavigationPublic(Long ktId, Long opsId, boolean esikatselu);

    @PreAuthorize("hasPermission(null, 'OPH','HALLINTA')")
    List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(Set<KoulutusTyyppi> koulutusTyyppi);

    @PreAuthorize("isAuthenticated() or @profileService.isDevProfileActive()")
    Page<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(Integer sivu, Integer sivukoko);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, OpsTyyppi tyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, Set<String> koulutustyypit, Set<Tila> tilat);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, Set<String> koulutustyypit, OpsTyyppi opsTyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    Page<OpetussuunnitelmaListausDto> getOpetussuunnitelmat(@P("ktId") Long ktId, OpsHakuInternalDto query);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaBaseDto> getPohjat(Long ktId, Set<Tila> tilat, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaBaseDto> getOphOpsPohjat(Set<KoulutusTyyppi> koulutustyypit);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUONTI')")
    void setOpsCommon(Long ktId, Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv);

    @PreAuthorize("permitAll()")
    Object getJulkaistuSisaltoObjectNode(Long opetussuunnitelmaId, List<String> queryList);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaDto> getKaikkiJulkaistutOpetussuunnitelmat();
}

