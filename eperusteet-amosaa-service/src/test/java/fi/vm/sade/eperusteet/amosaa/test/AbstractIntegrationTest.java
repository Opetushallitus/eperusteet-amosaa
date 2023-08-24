package fi.vm.sade.eperusteet.amosaa.test;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractIntegrationTest {

    static public String KP1 = "kp1";
    static public String KP2 = "kp2";
    static public String TMPR = "tmpr";

    static public String oidOph = "1.2.246.562.10.00000000001";
    static public String oidKp1 = "1.2.246.562.10.54645809036";
    static public String oidKp2 = "1.2.246.562.10.2013120512391252668625";
    static public String oidKp3 = "1.2.246.562.10.2013120513110198396408";
    static public String oidTmpr = "1.2.246.562.10.79499343246";

    static public String KP7_KAYTTAJA_OID = "1.2.3.4.5.kp1";
    static public String KP8_KAYTTAJA_OID = "1.22.3.4.5.kp2";
    static public String KPTMPR_KAYTTAJA_OID = "1.22.3.4.5.TMPR";

    @Autowired
    protected OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    protected KoulutustoimijaService koulutustoimijaService;

    @Autowired
    protected KayttajanTietoService kayttajanTietoService;

    @Autowired
    protected OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    protected KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    protected SisaltoViiteService sisaltoViiteService;

    protected List<KoulutustoimijaBaseDto> koulutustoimijat = new ArrayList<>();
    protected KoulutustoimijaBaseDto toimija = null;
    protected Kayttaja kayttaja = null;

    protected Long getKoulutustoimijaId() {
        return getKoulutustoimija().getId();
    }

    protected KoulutustoimijaBaseDto getKoulutustoimija() {
        return toimija;
    }

    protected Koulutustoimija koulutustoimija() {
        return koulutustoimijaRepository.findOne(getKoulutustoimijaId());
    }

    protected KayttajaoikeusDto updateUserOikeus(Long ktId, Long opsId, KayttajaoikeusTyyppi oikeus, String kayttajaOid) {
        KayttajaDto user = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId()).stream()
                .filter(kayttaja -> kayttaja.getOid().equals(kayttajaOid))
                .findFirst()
                .get();
        KayttajaoikeusDto result = new KayttajaoikeusDto();
        result.setOikeus(oikeus);
        result.setKayttaja(Reference.of(user.getId()));
        result = opetussuunnitelmaService.updateOikeus(getKoulutustoimijaId(), opsId, user.getId(), result);
        return result;
    }


    protected OpetussuunnitelmaBaseDto createOpetussuunnitelma() {
        return createOpetussuunnitelma((ops) -> {
        });
    }

    protected Opetussuunnitelma createOpetussuunnitelmaJulkaistu() {
        OpetussuunnitelmaBaseDto dto = createOpetussuunnitelma();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(dto.getId());
        ops.setTila(Tila.JULKAISTU);
        return opetussuunnitelmaRepository.save(ops);
    }

    protected Opetussuunnitelma updateOpetussuunnitelmaJulkaisukielet(Opetussuunnitelma opetussuunnitelma, Set<Kieli> kielet) {
        opetussuunnitelma.setJulkaisukielet(kielet);
        return opetussuunnitelmaRepository.save(opetussuunnitelma);
    }

    protected void updateKoulutustoimijaLokalisointiNimet(Map<Kieli, String> tekstit) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(toimija.getId());
        koulutustoimija.setNimi(LokalisoituTeksti.of(tekstit));
        koulutustoimijaRepository.save(koulutustoimija);
    }

    protected OpetussuunnitelmaBaseDto createOpetussuunnitelmaJulkaistu(Consumer<OpetussuunnitelmaLuontiDto> opsfn) {
        OpetussuunnitelmaBaseDto dto = createOpetussuunnitelma(opsfn);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(dto.getId());
        ops.setTila(Tila.JULKAISTU);
        opetussuunnitelmaRepository.save(ops);
        return opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), dto.getId());
    }

    protected OpetussuunnitelmaBaseDto createOpetussuunnitelma(Consumer<OpetussuunnitelmaLuontiDto> opsfn) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setPerusteDiaarinumero("9/011/2008");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        HashMap<String, String> nimi = new HashMap<>();
        nimi.put("fi", "auto");
        ops.setNimi(new LokalisoituTekstiDto(nimi));
        ops.setPerusteId(515491l);
        opsfn.accept(ops);
        return opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
    }

    protected OpetussuunnitelmaBaseDto createPohja() {
        return createPohja((ops) -> {
        });
    }

    protected OpetussuunnitelmaBaseDto createPohja(Consumer<OpetussuunnitelmaDto> opsfn) {
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setTyyppi(OpsTyyppi.POHJA);
        HashMap<String, String> nimi = new HashMap<>();
        nimi.put("fi", "auto");
        ops.setNimi(new LokalisoituTekstiDto(nimi));
        ops.setJulkaisukielet(new HashSet<Kieli>() {{
            add(Kieli.FI);
        }});
        opsfn.accept(ops);
        OpetussuunnitelmaBaseDto pohja = opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
        return pohja;
    }

    protected SisaltoViiteDto.Matala createSisalto() {
        return createSisalto((ops) -> {
        });
    }

    protected SisaltoViiteDto.Matala createSisalto(Consumer<SisaltoViiteDto> sisaltoFn) {
        SisaltoViiteDto.Matala result = new SisaltoViiteDto.Matala();
        result.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        sisaltoFn.accept(result);
        return result;
    }

    @Before
    public void useProfileTest() {
        useProfileKP2();
    }

    private void updateProfile(String username) {
        updateProfile(username, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA);
    }

    private void updateProfile(String username, PermissionEvaluator.RolePrefix rolePrefix) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken(username, "test"));
        SecurityContextHolder.setContext(ctx);
        kayttajanTietoService.updateKoulutustoimijat(rolePrefix);
        String userOrgOid = kayttajanTietoService.getUserOrganizations(rolePrefix).stream().findFirst().get();
        this.koulutustoimijat = kayttajanTietoService.koulutustoimijat(rolePrefix);
        this.toimija = this.koulutustoimijat.stream().filter(kt -> kt.getOrganisaatio().equals(userOrgOid)).findFirst().get();
        kayttaja = kayttajanTietoService.getKayttaja();
    }

    protected void useProfileKP1() {
        updateProfile("kp1");
    }

    protected void useProfileKP2() {
        updateProfile("kp2");
    }

    protected void useProfileKP2user2() {
        updateProfile("kp2user2");
    }

    protected void useProfileKP3() {
        updateProfile("kp3");
    }

    protected void useProfileOPH() {
        updateProfile("oph");
    }

    protected void useProfileOPH(PermissionEvaluator.RolePrefix rolePrefix) {
        updateProfile("oph", rolePrefix);
    }

    protected void useProfileTmpr() {
        updateProfile("tmpr");
    }

    protected void useProfileTuva() {
        updateProfile("tuva", PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_TUVA);
    }

    protected void useProfileVst() {
        updateProfile("vst", PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_VST);
    }

    protected void useProfileKoto() {
        updateProfile("koto", PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_KOTO);
    }

    protected void regAllProfiles() {
        useProfileOPH();
        useProfileKP1();
        useProfileKP2();
        useProfileKP2user2();
        useProfileKP3();
        useProfileTmpr();
    }

    protected void setCurrentProfileRyhma() {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.getOne(getKoulutustoimijaId());
        koulutustoimija.setOrganisaatioRyhma(true);
        koulutustoimijaRepository.save(koulutustoimija);
    }

    public SisaltoViiteKevytDto getFirstOfType(Long ktId, Long opsId, SisaltoTyyppi tyyppi) {
        List<SisaltoViiteKevytDto> sisaltoViitteet = sisaltoViiteService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
        return sisaltoViitteet.stream()
                .filter(sv -> sv.getTyyppi().equals(tyyppi))
                .findAny().get();
    }

    public List<SisaltoViiteKevytDto> getType(Long ktId, Long opsId, SisaltoTyyppi tyyppi) {
        List<SisaltoViiteKevytDto> sisaltoViitteet = sisaltoViiteService.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
        return sisaltoViitteet.stream()
                .filter(sv -> sv.getTyyppi().equals(tyyppi))
                .collect(Collectors.toList());
    }

}
