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

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.SuorituspolkuRivi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * @author nkala
 * <p>
 * Suorituspolku on erikoistettu versio muodostumissäännöstöstä.
 */
@Entity
@Audited
@Table(name = "suorituspolku")
public class Suorituspolku extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "suorituspolku")
    private Set<SuorituspolkuRivi> rivit = new HashSet<>();

    @Getter
    @Setter
    private Boolean naytaKuvausJulkisesti;

    public static Suorituspolku copy(Suorituspolku suorituspolku) {
        if (suorituspolku != null) {
            suorituspolku.setId(null);

            for (SuorituspolkuRivi rivi : suorituspolku.getRivit()) {
                rivi.setSuorituspolku(suorituspolku);
                rivi.setId(null);
            }
        }
        return suorituspolku;
    }
}
