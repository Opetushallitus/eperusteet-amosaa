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
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Koulutuksenosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KoulutuksenosanPaikallinenTarkennus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TuvaLaajaAlainenOsaaminen;
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

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author mikkom
 */
@Entity
@Audited
@Table(name = "sisaltoviite")
public class SisaltoViite implements ReferenceableEntity, Serializable, Copyable<SisaltoViite> {
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

    @ManyToOne
    @Getter
    private TekstiKappale pohjanTekstikappale;

    @Getter
    @Setter
    @Column(name = "nayta_pohjan_teksti")
    private boolean naytaPohjanTeksti = true;

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

    @Getter
    @Setter
    @Column(name = "nayta_perusteen_teksti")
    private boolean naytaPerusteenTeksti;

    // Jos tutkinnon osa on tuotu, opsilla ei ole muuten tietoa perusteen osasta
    // Jos null, käytetään valittua opsia
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CachedPeruste peruste;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Opintokokonaisuus opintokokonaisuus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Koulutuksenosa koulutuksenosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private TuvaLaajaAlainenOsaaminen tuvaLaajaAlainenOsaaminen;

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

    @Override
    public SisaltoViite copy(boolean deep) {
        return copy(this, deep, true);
    }

    public SisaltoViite copy(boolean deep, boolean copyText) {
        return copy(this, deep, copyText);
    }

    public static SisaltoViite copy(SisaltoViite kopioitava) {
        return copy(kopioitava, true, true);
    }

    public static SisaltoViite copy(SisaltoViite kopioitava, boolean copyChildren) {
        return copy(kopioitava, copyChildren, true);
    }

    public static SisaltoViite copy(SisaltoViite original, boolean copyChildren, boolean copyText) {
        if (original != null) {
            SisaltoViite result = new SisaltoViite();
            result.setLiikkumaton(original.isLiikkumaton());
            result.setPakollinen(original.isPakollinen());
            result.setNaytaPerusteenTeksti(original.isNaytaPerusteenTeksti());
            result.setOhjeteksti(original.getOhjeteksti());
            result.setTyyppi(original.getTyyppi());
            result.setPerusteteksti(original.getPerusteteksti());
            result.setTosa(Tutkinnonosa.copy(original.getTosa()));
            result.setSuorituspolku(Suorituspolku.copy(original.getSuorituspolku()));
            result.setOpintokokonaisuus(Opintokokonaisuus.copy(original.getOpintokokonaisuus()));
            result.setKoulutuksenosa(Koulutuksenosa.copy(original.getKoulutuksenosa()));
            result.setTuvaLaajaAlainenOsaaminen(TuvaLaajaAlainenOsaaminen.copy(original.getTuvaLaajaAlainenOsaaminen()));
            result.setPeruste(original.getPeruste());

            result.setTekstiKappale(TekstiKappale.copy(original.getTekstiKappale()));
            if (!copyText && original.getTekstiKappale() != null) {
                result.getTekstiKappale().setTeksti(null);
                result.updatePohjanTekstikappale(original.getTekstiKappale());
                result.setNaytaPohjanTeksti(true);
            }

            if (copyChildren) {
                result.setLapset(new ArrayList<>());
                for (SisaltoViite lapsi : original.getLapset()) {
                    result.getLapset().add(copy(lapsi));
                }
            }
            return result;
        }
        else {
            return null;
        }
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

    static public SisaltoViite createTekstikappale(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        return result;
    }

    static public SisaltoViite createOpintokokonaisuus(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
        Opintokokonaisuus opintokokonaisuus = new Opintokokonaisuus();
        result.setOpintokokonaisuus(opintokokonaisuus);
        return result;
    }

    static public SisaltoViite createKoulutuksenosa(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.KOULUTUKSENOSA);
        Koulutuksenosa koulutuksenosa = new Koulutuksenosa();
        result.setKoulutuksenosa(koulutuksenosa);
        return result;
    }

    static public SisaltoViite createTuvaLaajaAlainenOsaaminen(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.LAAJAALAINENOSAAMINEN);
        TuvaLaajaAlainenOsaaminen tuvaLaajaAlainenOsaaminen = new TuvaLaajaAlainenOsaaminen();
        result.setTuvaLaajaAlainenOsaaminen(tuvaLaajaAlainenOsaaminen);
        return result;
    }

    public void updatePohjanTekstikappale(TekstiKappale other) {
        this.pohjanTekstikappale = other;
    }
}
