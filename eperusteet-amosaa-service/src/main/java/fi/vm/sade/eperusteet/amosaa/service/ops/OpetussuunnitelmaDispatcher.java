package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OpetussuunnitelmaDispatcher {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private List<OpetussuunnitelmaToteutus> kaikkiToteutukset;

    private HashMap<Class, OpetussuunnitelmaToteutus> defaults = new HashMap<>();
    private Map<Class, HashMap<OpsTyyppi, OpetussuunnitelmaToteutus>> opsTyyppiMap = new HashMap<>();
    private Map<Class, HashMap<KoulutusTyyppi, OpetussuunnitelmaToteutus>> toteutuksetMap = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        for (OpetussuunnitelmaToteutus toteutus : kaikkiToteutukset) {
            Set<KoulutusTyyppi> toteutukset = toteutus.getTyypit();
            Set<OpsTyyppi> opsTyypit = toteutus.getOpsTyypit();
            Class impl = toteutus.getImpl();
            if (opsTyypit.isEmpty() && toteutukset.isEmpty()) {
                defaults.put(impl, toteutus);
            } else if (!toteutukset.isEmpty()){
                if (!toteutuksetMap.containsKey(impl)) {
                    toteutuksetMap.put(impl, new HashMap<>());
                }
                HashMap<KoulutusTyyppi, OpetussuunnitelmaToteutus> map = toteutuksetMap.get(impl);
                toteutukset.forEach(t -> {
                    map.put(t, toteutus);
                });
            }
            else {
                if (!opsTyyppiMap.containsKey(impl)) {
                    opsTyyppiMap.put(impl, new HashMap<>());
                }
                HashMap<OpsTyyppi, OpetussuunnitelmaToteutus> map = opsTyyppiMap.get(impl);
                opsTyypit.forEach(t -> {
                    map.put(t, toteutus);
                });
            }
        }
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(Long opsId, Class<T> clazz) {
        Opetussuunnitelma p = opetussuunnitelmaRepository.findOne(opsId);
        if (p == null) {
            throw new BusinessRuleViolationException("Opetussuunnitelmaa ei ole");
        }
        if (p.getPeruste() != null) {
            return get(p.getPeruste().getKoulutustyyppi(), clazz);
        }

        if (p.getKoulutustyyppi() != null) {
            return get(p.getKoulutustyyppi(), clazz);
        }
        
        return get(clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(Class<T> clazz) {
        return get((KoulutusTyyppi) null, clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(KoulutusTyyppi toteutus, Class<T> clazz) {
        return get(toteutus, null, clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(KoulutusTyyppi toteutus, OpsTyyppi opsTyyppi, Class<T> clazz) {
        if (opsTyyppi != null) {
            HashMap<OpsTyyppi, OpetussuunnitelmaToteutus> toteutukset = this.opsTyyppiMap.getOrDefault(clazz, null);
            if (toteutukset != null && toteutukset.containsKey(opsTyyppi)) {
                OpetussuunnitelmaToteutus impl = toteutukset.getOrDefault(opsTyyppi, null);
                if (impl != null) {
                    return (T) impl;
                }
            }
        }

        if (toteutus != null) {
            HashMap<KoulutusTyyppi, OpetussuunnitelmaToteutus> toteutukset = this.toteutuksetMap.getOrDefault(clazz, null);
            if (toteutukset != null && toteutukset.containsKey(toteutus)) {
                OpetussuunnitelmaToteutus impl = toteutukset.getOrDefault(toteutus, null);
                if (impl != null) {
                    return (T) impl;
                }
            }
        }
        OpetussuunnitelmaToteutus impl = defaults.getOrDefault(clazz, null);
        if (impl != null) {
            return (T) impl;
        }
        throw new BusinessRuleViolationException("Toteutusta ei löytynyt: "
                + clazz.getSimpleName()
                + " " + (toteutus != null ? toteutus.toString() : ""));
    }

}
