package fi.vm.sade.eperusteet.amosaa.domain.arviointi;

import fi.vm.sade.eperusteet.amosaa.domain.Osaamistaso;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity
@Cacheable
@Table(name = "arviointiasteikko")
public class Arviointiasteikko implements Serializable, ReferenceableEntity {

    @Id
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn
    private List<Osaamistaso> osaamistasot;

}
