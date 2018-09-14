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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * @author nkala
 * Suorituspolku on erikoistettu versio muodostumissäännöstöstä.
 */
@Entity
@Audited
@Table(name = "suorituspolku")
public class Suorituspolku extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<Suorituspolku> {
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

    @Getter
    @Setter
    private Boolean osasuorituspolku;

    @Getter
    @Setter
    @Column(precision = 10, scale = 2, name = "osasuorituspolku_laajuus")
    private BigDecimal osasuorituspolkuLaajuus;

    @Override
    public Suorituspolku copy(boolean deep) {
        return copy(this);
    }

    public static Suorituspolku copy(Suorituspolku original) {
        if (original != null) {
            Suorituspolku result = new Suorituspolku();
            result.setNaytaKuvausJulkisesti(original.getNaytaKuvausJulkisesti());

            for (SuorituspolkuRivi rivi : original.getRivit()) {
                SuorituspolkuRivi copy = rivi.copy();
                copy.setSuorituspolku(result);
                result.getRivit().add(copy);
            }
            return result;
        }
        else {
            return null;
        }
    }
}
