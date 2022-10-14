/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * @author jhyoty
 */
public class LokalisoituTekstiDto {

    @Getter
    private final Long id;

    @Getter
    private UUID tunniste;

    @Getter
    private final Map<Kieli, String> tekstit;

    public LokalisoituTekstiDto(Long id, Map<Kieli, String> values) {
        this(id, null, values);
    }

    public LokalisoituTekstiDto(Long id, UUID tunniste, Map<Kieli, String> values) {
        this.id = id;
        this.tunniste = tunniste;
        this.tekstit = values == null ? null : new EnumMap<>(values);
    }

    static public LokalisoituTekstiDto of(String teksti) {
        return new LokalisoituTekstiDto(null, new HashMap<Kieli, String>() {{
            put(Kieli.FI, teksti);
        }});
    }

    static public LokalisoituTekstiDto of(Kieli kieli, String teksti) {
        return new LokalisoituTekstiDto(null, new HashMap<Kieli, String>() {{
            put(kieli, teksti);
        }});
    }

    @JsonCreator
    public LokalisoituTekstiDto(Map<String, String> values) {
        Long tmpId = null;
        EnumMap<Kieli, String> tmpValues = new EnumMap<>(Kieli.class);

        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if ("_id".equals(entry.getKey())) {
                    tmpId = Long.valueOf(entry.getValue());
                } else if ("_tunniste".equals(entry.getKey())) {
                    this.tunniste = UUID.fromString(entry.getValue());
                } else {
                    Kieli k = Kieli.of(entry.getKey());
                    tmpValues.put(k, entry.getValue());
                }
            }
        }

        this.id = tmpId;
        this.tekstit = tmpValues;
    }

    @JsonValue
    public Map<String, String> asMap() {
        HashMap<String, String> map = new HashMap<>();
        if (id != null) {
            map.put("_id", id.toString());
        }
        if (tunniste != null) {
            map.put("_tunniste", tunniste.toString());
        }
        for (Map.Entry<Kieli, String> e : tekstit.entrySet()) {
            map.put(e.getKey().toString(), e.getValue());
        }
        return map;
    }

    @JsonIgnore
    public String get(Kieli kieli) {
        return tekstit.get(kieli);
    }

    public Map<Kieli, String> getTeksti() {
        EnumMap<Kieli, String> map = new EnumMap<>(Kieli.class);
        for (Map.Entry<Kieli, String> t : tekstit.entrySet()) {
            map.put(t.getKey(), t.getValue());
        }
        return map;
    }

    @SuppressWarnings("DtoClassesNotContainEntities")
    public static <K> Map<K, Optional<LokalisoituTekstiDto>> ofOptionalMap(Map<K, Optional<LokalisoituTeksti>> map) {
        Map<K, Optional<LokalisoituTekstiDto>> result = new HashMap<>();
        for (Map.Entry<K, Optional<LokalisoituTeksti>> kv : map.entrySet()) {
            result.put(kv.getKey(), kv.getValue().map(teksti -> new LokalisoituTekstiDto(teksti.getId(), teksti.getTunniste(), teksti.getTeksti())));
        }
        return result;
    }

    public static LokalisoituTekstiDto of(JsonNode node) {
        if (node == null) {
            return null;
        }

        Map<String, String> nimet = new HashMap<>();
        if (node.has("fi")) {
            nimet.put(Kieli.FI.toString(), node.get("fi").asText());
        }
        if (node.has("en")) {
            nimet.put(Kieli.EN.toString(), node.get("en").asText());
        }
        if (node.has("sv")) {
            nimet.put(Kieli.SV.toString(), node.get("sv").asText());
        }
        return new LokalisoituTekstiDto(nimet);
    }
}
