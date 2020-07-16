package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
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
    private Map<Class, HashMap<KoulutusTyyppi, OpetussuunnitelmaToteutus>> toteutuksetMap = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        for (OpetussuunnitelmaToteutus toteutus : kaikkiToteutukset) {
            Set<KoulutusTyyppi> toteutukset = toteutus.getTyypit();
            Class impl = toteutus.getImpl();
            if (toteutukset.isEmpty()) {
                defaults.put(impl, toteutus);
            } else {
                if (!toteutuksetMap.containsKey(impl)) {
                    toteutuksetMap.put(impl, new HashMap<>());
                }
                HashMap<KoulutusTyyppi, OpetussuunnitelmaToteutus> map = toteutuksetMap.get(impl);
                toteutukset.forEach(t -> {
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
        } else {
            return get(clazz);
        }
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(Class<T> clazz) {
        return get((KoulutusTyyppi) null, clazz);
    }

    @PreAuthorize("permitAll()")
    public <T extends OpetussuunnitelmaToteutus> T get(KoulutusTyyppi toteutus, Class<T> clazz) {
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
