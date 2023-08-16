package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.ValidointiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Validointi {

    private ValidationCategory kategoria;
    private List<Virhe> virheet = new ArrayList<>();
    private List<Virhe> huomautukset = new ArrayList<>();
    private List<Virhe> huomiot = new ArrayList<>();

    public Validointi(ValidationCategory kategoria) {
        this.kategoria = kategoria;
    }

    @Getter
    @AllArgsConstructor
    static public class Virhe {
        private String kuvaus;
        private Map<Kieli, String> nimi;
        private NavigationNodeDto navigationNode;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode) {
        virheet.add(new Virhe(kuvaus, null, navigationNode));
        return this;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode, Map<Kieli, String> nimi) {
        virheet.add(new Virhe(kuvaus, nimi, navigationNode));
        return this;
    }

    public Validointi huomautukset(String kuvaus, NavigationNodeDto navigationNode) {
        huomautukset.add(new Virhe(kuvaus, null, navigationNode));
        return this;
    }

    public Validointi huomautukset(String kuvaus, NavigationNodeDto navigationNode, Map<Kieli, String> nimi) {
        huomautukset.add(new Virhe(kuvaus, nimi, navigationNode));
        return this;
    }

    public void tuomitse() {
        if (!virheet.isEmpty()) {
            throw new ValidointiException(this);
        }
    }
}
