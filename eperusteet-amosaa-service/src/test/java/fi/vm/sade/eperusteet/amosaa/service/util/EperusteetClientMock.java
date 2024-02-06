package fi.vm.sade.eperusteet.amosaa.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.PalauteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;

import java.util.*;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@Profile("test")
@SuppressWarnings("TransactionalAnnotations")
@Transactional
public class EperusteetClientMock implements EperusteetClient {

    private ObjectMapper objectMapper;
    private List<JsonNode> perusteet = new ArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    protected void init() {
        objectMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        perusteet.add(openFakeData("/perusteet/kotoPeruste.json"));
        perusteet.add(openFakeData("/perusteet/amosaaPeruste.json"));
        perusteet.add(openFakeData("/perusteet/tuvaPeruste.json"));
        perusteet.add(openFakeData("/perusteet/vstPeruste.json"));
        perusteet.add(openFakeData("/perusteet/amosaaPeruste2.json"));
        perusteet.add(openFakeData("/perusteet/amosaaPerusteKoulutuskoodiVanha.json"));
        perusteet.add(openFakeData("/perusteet/amosaaPerusteKoulutuskoodiUusi.json"));
        perusteet.add(openFakeData("/perusteet/amosaaPerusteKoulutuskoodiUusi2.json"));
        perusteet.add(openFakeData("/perusteet/paivitettavaAmmatillinenPeruste.json"));
        perusteet.add(openFakeData("/perusteet/vstPerustePaivitettava.json"));
        perusteet.add(openFakeData("/perusteet/vstPerustePaivitetty.json"));
    }

    public static final String DIAARINUMERO = "mock-diaarinumero";

    private JsonNode openFakeData(String file) {
        try {
            JsonNode result = objectMapper.readTree(getClass().getResourceAsStream(file));
            return result;
        } catch (IOException e) {
            throw new BusinessRuleViolationException("datan-hakeminen-epaonnistui", e);
        }
    }

    @Override
    public ArviointiasteikkoDto getArviointiasteikko(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getPeruste(String diaariNumero, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public PerusteDto getYleinenPohja() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getYleinenPohjaSisalto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Autowired
    private DtoMapper mapper;

    private JsonNode getMockPeruste(Long id) {
        return perusteet.stream().filter(p -> Objects.equals(p.get("id").asLong(), id)).findFirst().orElseThrow(() -> new BusinessRuleViolationException("tiedostoa ei löytynyt"));
    }

    @Override
    public String getPerusteData(Long id) {
        try {
            JsonNode node = getMockPeruste(id);
            Object perusteObj = objectMapper.treeToValue(node, Object.class);
            String json = objectMapper.writeValueAsString(perusteObj);
            return json;
        } catch (IOException ex) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }
    }

    @Override
    public List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        PerusteDto peruste = new PerusteDto();
        peruste.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(peruste);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        return null;
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        return null;
    }

    @Override
    public <T> T getPeruste(Long id, Class<T> type) {
        try {
            JsonNode perusteJson = getMockPeruste(id);
            T result = objectMapper.treeToValue(perusteJson, type);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessRuleViolationException("tiedostoa ei löytynyt");
        }
    }

    @Override
    public JsonNode findFromPerusteet(Map<String, String> query) {
        return null;
    }

    @Override
    public PalauteDto lahetaPalaute(PalauteDto palaute) throws JsonProcessingException {
        return null;
    }

    @Override
    public Date getViimeisinJulkaisuPeruste(Long perusteId) {
        return DateTime.now().toDate();
    }

    @Override
    public List<PerusteDto> findPerusteetByKoulutuskoodit(List<String> koulutuskoodit) {
        if (koulutuskoodit.contains("koulutus_381405")) {
            try {
                return Arrays.asList(
                        objectMapper.treeToValue(openFakeData("/perusteet/amosaaPerusteKoulutuskoodiUusi.json"), PerusteDto.class),
                        objectMapper.treeToValue(openFakeData("/perusteet/amosaaPerusteKoulutuskoodiUusi2.json"), PerusteDto.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> T getPerusteOrNull(Long id, Class<T> type) {
        return null;
    }
}
