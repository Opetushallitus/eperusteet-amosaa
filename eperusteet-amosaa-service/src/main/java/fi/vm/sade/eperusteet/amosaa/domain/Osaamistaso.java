package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Cacheable
@Table(name = "osaamistaso")
public class Osaamistaso implements Serializable, ReferenceableEntity {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private LokalisoituTeksti otsikko;

    @Override
    public String toString() {
        return "" + id;
    }
}
