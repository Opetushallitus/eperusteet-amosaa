package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Table(name = "lokalisoituteksti")
public class LokalisoituTeksti implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Getter
    @Column(updatable = false)
    private UUID tunniste;

    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Immutable
    @CollectionTable(name = "lokalisoituteksti_teksti")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Teksti> teksti;

    protected LokalisoituTeksti() {
    }

    private LokalisoituTeksti(Set<Teksti> tekstit, UUID tunniste) {
        this.teksti = tekstit;
        this.tunniste = tunniste != null ? tunniste : UUID.randomUUID();
    }

    public Map<Kieli, String> getTeksti() {
        EnumMap<Kieli, String> map = new EnumMap<>(Kieli.class);
        for (Teksti t : teksti) {
            map.put(t.getKieli(), t.getTeksti());
        }
        return map;
    }

    public Map<String, String> getTekstiAsStringMap() {
        Map<String, String> map = new HashMap<>();
        for (Teksti t : teksti) {
            map.put(t.getKieli().toString(), t.getTeksti());
        }
        return map;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.teksti);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof LokalisoituTeksti) {
            final LokalisoituTeksti other = (LokalisoituTeksti) obj;
            return Objects.equals(this.teksti, other.teksti);
        }
        return false;
    }

    public static LokalisoituTeksti of(Map<Kieli, String> tekstit, UUID tunniste) {
        if (tekstit == null) {
            return null;
        }
        HashSet<Teksti> tmp = new HashSet<>(tekstit.size());
        for (Map.Entry<Kieli, String> e : tekstit.entrySet()) {
            if (e.getValue() != null) {
                String v = Normalizer.normalize(e.getValue().trim(), Normalizer.Form.NFC);
                if (!v.isEmpty()) {
                    tmp.add(new Teksti(e.getKey(), v));
                }
            }
        }
        if (tmp.isEmpty()) {
            return null;
        }

        return new LokalisoituTeksti(tmp, tunniste);
    }

    public static LokalisoituTeksti of(Map<Kieli, String> tekstit) {
        return of(tekstit, null);
    }

    public static LokalisoituTeksti of(LokalisoituTekstiDto tekstiDto) {
        if (tekstiDto == null) {
            return null;
        }
        return of(tekstiDto.getTekstit());
    }

    public static LokalisoituTeksti of(JsonNode node) {
        if (node == null) {
            return null;
        }

        Map<Kieli, String> nimet = new HashMap<>();
        if (node.has("fi")) {
            nimet.put(Kieli.FI, node.get("fi").asText());
        }
        if (node.has("en")) {
            nimet.put(Kieli.EN, node.get("en").asText());
        }
        if (node.has("sv")) {
            nimet.put(Kieli.SV, node.get("sv").asText());
        }
        return of(nimet);
    }

    public static LokalisoituTeksti of(Kieli kieli, String teksti) {
        return of(Collections.singletonMap(kieli, teksti));
    }

    @Override
    public String toString() {
        Map<Kieli, String> tekstit = getTeksti();
        if (tekstit.isEmpty()) {
            return "";
        }

        return tekstit.entrySet().stream()
                .filter(x -> x.getValue() != null)
                .map(x -> x.getKey().toString() + ": " + x.getValue() + ", ")
                .collect(Collectors.joining());
    }

    public boolean hasKielet(Set<Kieli> kielet) {
        boolean hasSomething = false;
        Map<Kieli, String> mteksti = getTeksti();

        for (Kieli kieli : kielet) {
            String str = mteksti.get(kieli);
            if (str != null && !str.isEmpty()) {
                hasSomething = true;
                break;
            }
        }

        if (hasSomething) {
            for (Kieli kieli : kielet) {
                String sisalto = mteksti.get(kieli);
                if (sisalto == null || sisalto.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    static public void validoi(Validointi validointi, Opetussuunnitelma ops, LokalisoituTeksti teksti, NavigationNodeDto navigationNodeDto) {
        validoi("kielisisaltoa-ei-loytynyt-opsin-kielilla", validointi, ops, teksti, navigationNodeDto);
    }

    static public void validoi(String kuvaus, Validointi validointi, Opetussuunnitelma ops, LokalisoituTeksti teksti, NavigationNodeDto navigationNodeDto) {
        if (teksti == null || !teksti.hasKielet(ops.getJulkaisukielet())) {
            validointi.virhe(kuvaus, navigationNodeDto);
        }
    }
}
