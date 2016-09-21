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
package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 *
 * @author mikkom
 */
@Entity
@Audited
@Table(name = "sisaltoviite")
public class SisaltoViite implements ReferenceableEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private boolean pakollinen;

    @Getter
    @Setter
    private boolean valmis;

    @Getter
    @Setter
    private boolean liikkumaton; // Jos tosi, solmun paikkaa ei voi vaihtaa rakennepuussa

    @Getter
    @Setter
    private Long versio;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private SisaltoTyyppi tyyppi = SisaltoTyyppi.TEKSTIKAPPALE;

    @ManyToOne
    @Getter
    @Setter
    private SisaltoViite vanhempi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    private TekstiKappale tekstiKappale;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti ohjeteksti;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti perusteteksti;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Opetussuunnitelma owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Tutkinnonosa tosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Suorituspolku suorituspolku;

    @OneToMany(mappedBy = "vanhempi", fetch = FetchType.LAZY)
    @OrderColumn
    @Getter
    @Setter
    @BatchSize(size = 100)
    private List<SisaltoViite> lapset = new ArrayList<>();

    public SisaltoViite() {
    }

    public SisaltoViite(Opetussuunnitelma owner) {
        this.owner = owner;
    }

    public SisaltoViite getRoot() {
        SisaltoViite root = this;
        while (root.getVanhempi() != null) {
            root = root.getVanhempi();
        }
        return root;
    }

    public static SisaltoViite copy(SisaltoViite kopioitava) {
        return copy(kopioitava, true);
    }

    public static SisaltoViite copy(SisaltoViite kopioitava, boolean copyChildren) {
        if (kopioitava != null) {
            kopioitava.setId(null);
            kopioitava.setVanhempi(null);

            kopioitava.setTekstiKappale(TekstiKappale.copy(kopioitava.getTekstiKappale()));
            kopioitava.setTosa(Tutkinnonosa.copy(kopioitava.getTosa()));
            kopioitava.setSuorituspolku(Suorituspolku.copy(kopioitava.getSuorituspolku()));

            if (copyChildren) {
                List<SisaltoViite> lapset = kopioitava.getLapset();
                kopioitava.setLapset(new ArrayList<>());
                for (SisaltoViite lapsi : lapset) {
                    kopioitava.getLapset().add(copy(lapsi));
                }
            }
        }
        return kopioitava;
    }

    static private SisaltoViite createCommon(SisaltoViite parent) {
        SisaltoViite result = new SisaltoViite();
        TekstiKappale tk = new TekstiKappale();
        result.setTekstiKappale(tk);
        result.setVanhempi(parent);
        result.setOwner(parent.getOwner());
        parent.getLapset().add(result);
        return result;
    }

    static public SisaltoViite createTutkinnonOsa(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setLiikkumaton(true);
        result.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
        Tutkinnonosa tosa = new Tutkinnonosa();
        result.setTosa(tosa);
        return result;
    }
}
