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

package fi.vm.sade.eperusteet.amosaa.domain.arviointi;

import fi.vm.sade.eperusteet.amosaa.domain.Osaamistaso;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author teele1
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Table(name = "arviointiasteikko")
public class Arviointiasteikko implements Serializable, ReferenceableEntity {

    @Id
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn
    @Immutable
    private List<Osaamistaso> osaamistasot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Osaamistaso> getOsaamistasot() {
        return osaamistasot;
    }

    public void setOsaamistasot(List<Osaamistaso> osaamistasot) {
        this.osaamistasot = osaamistasot;
    }
}
