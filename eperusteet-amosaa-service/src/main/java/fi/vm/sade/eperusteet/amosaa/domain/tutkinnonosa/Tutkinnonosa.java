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
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

/**
 * @author nkala
 */
@Entity
@Audited
@Table(name = "tutkinnonosa")
public class Tutkinnonosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private TutkinnonosaTyyppi tyyppi;

    @Getter
    @Setter
    @Column(updatable = false)
    private String koodi;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long perusteentutkinnonosa; // FIXME Käytä mahdollisesti tunnistetta

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti osaamisenOsoittaminen;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private OmaTutkinnonosa omatutkinnonosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private VierasTutkinnonosa vierastutkinnonosa;

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<TutkinnonosaToteutus> toteutukset = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<VapaaTeksti> vapaat = new ArrayList<>();

    public static Tutkinnonosa copy(Tutkinnonosa original) {
        if (original != null) {
            Tutkinnonosa result = new Tutkinnonosa();

            result.setTyyppi(original.getTyyppi());
            result.setKoodi(original.getKoodi());
            result.setPerusteentutkinnonosa(original.getPerusteentutkinnonosa());
            result.setOsaamisenOsoittaminen(original.getOsaamisenOsoittaminen());
            result.setOmatutkinnonosa(OmaTutkinnonosa.copy(original.getOmatutkinnonosa()));
            result.setVierastutkinnonosa(VierasTutkinnonosa.copy(original.getVierastutkinnonosa()));

            List<TutkinnonosaToteutus> toteutukset = original.getToteutukset();
            if (!ObjectUtils.isEmpty(toteutukset)) {
                result.setToteutukset(new ArrayList<>());
                for (TutkinnonosaToteutus toteutus : toteutukset) {
                    TutkinnonosaToteutus copy = toteutus.copy();
                    copy.setTutkinnonosa(result);
                    result.getToteutukset().add(copy);
                }
            }

            List<VapaaTeksti> vapaat = original.getVapaat();
            if (!ObjectUtils.isEmpty(vapaat)) {
                result.setVapaat(new ArrayList<>());
                for (VapaaTeksti vapaa : vapaat) {
                    result.getVapaat().add(vapaa.copy());
                }
            }

            return result;
        } else {
            return null;
        }
    }
}
