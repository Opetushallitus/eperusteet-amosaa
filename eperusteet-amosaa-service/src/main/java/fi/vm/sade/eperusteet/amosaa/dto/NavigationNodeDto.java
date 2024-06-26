package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavigationNodeDto {
    private Long id;
    private LokalisoituTekstiDto label;
    private NavigationType type;
    private Map<String, Object> meta = new HashMap<>();
    private List<NavigationNodeDto> children = new ArrayList<>();

    static public NavigationNodeDto of(NavigationType type, LokalisoituTekstiDto label, Long id) {
        NavigationNodeDto result = new NavigationNodeDto();
        result.setType(type);
        result.setLabel(label);
        result.setId(id);
        return result;
    }

    static public NavigationNodeDto of(NavigationType type, LokalisoituTekstiDto label) {
        NavigationNodeDto result = new NavigationNodeDto();
        result.setType(type);
        result.setLabel(label);
        return result;
    }

    static public NavigationNodeDto of(NavigationType type) {
        return of(type, null, null);
    }

    static public NavigationNodeDto of(NavigationType type, Long id) {
        return of(type, null, id);
    }

    public NavigationNodeDto meta(String key, Object value) {
        if (value != null) {
            meta.put(key, value);
        }
        return this;
    }

    public NavigationNodeDto add(NavigationNodeDto node) {
        if (node != null) {
            this.children.add(node);
        }
        return this;
    }

    public NavigationNodeDto addAll(Stream<NavigationNodeDto> nodes) {
        if (nodes != null) {
            this.children.addAll(nodes.collect(Collectors.toList()));
        }
        return this;
    }

    public NavigationNodeDto addAll(Collection<NavigationNodeDto> nodes) {
        if (nodes != null) {
            this.children.addAll(nodes);
        }
        return this;
    }

    public NavigationNodeDto addAll(NavigationNodeDto node) {
        if (node != null) {
            this.children.addAll(node.children);
        }
        return this;
    }

    public static NavigationNodeDto of(SisaltoViite sisaltoviite) {
        return NavigationNodeDto.of(
                NavigationType.of(sisaltoviite.getTyyppi().toString()),
                sisaltoviite.getNimi() != null ? LokalisoituTekstiDto.of(sisaltoviite.getNimi().getTeksti()) : null,
                sisaltoviite.getId());
    }

}
