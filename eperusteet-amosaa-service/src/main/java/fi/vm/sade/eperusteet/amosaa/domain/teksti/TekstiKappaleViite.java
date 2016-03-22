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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

/**
 *
 * @author mikkom
 */
@Entity
@Audited
@Table(name = "tekstikappaleviite")
public class TekstiKappaleViite implements ReferenceableEntity, Serializable {

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

    @ManyToOne
    @Getter
    @Setter
    private TekstiKappaleViite vanhempi;

    @ManyToOne
    @Getter
    @Setter
    private TekstiKappale tekstiKappale;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Opetussuunnitelma owner;

    @OneToMany(mappedBy = "vanhempi", fetch = FetchType.LAZY)
    @OrderColumn
    @Getter
    @Setter
    @BatchSize(size = 100)
    private List<TekstiKappaleViite> lapset = new ArrayList<>();

    public TekstiKappaleViite() {
    }

    public TekstiKappaleViite(Opetussuunnitelma owner) {
        this.owner = owner;
    }

    // Kopioi viitehierarkian ja siirtää irroitetut paikoilleen
    // UUID parentin tunniste
    public TekstiKappaleViite kopioiHierarkia(Map<UUID, TekstiKappaleViite> irroitetut) {
        TekstiKappaleViite result = new TekstiKappaleViite();
        result.setTekstiKappale(this.getTekstiKappale());
        result.setOwner(this.getOwner());

        if (lapset != null) {
            List<TekstiKappaleViite> ilapset = new ArrayList<>();
            for (TekstiKappaleViite lapsi : lapset) {
                TekstiKappaleViite uusiLapsi = lapsi.kopioiHierarkia(irroitetut);
                uusiLapsi.setVanhempi(result);
                ilapset.add(uusiLapsi);
            }
            for (Map.Entry<UUID, TekstiKappaleViite> lapsi : irroitetut.entrySet()) {
                if (this.getTekstiKappale().getTunniste() == lapsi.getKey()) {
                    ilapset.add(lapsi.getValue());
                    irroitetut.remove(lapsi.getKey());
                }
            }
            result.setLapset(ilapset);
        }
        return result;
    }

    public TekstiKappaleViite getRoot() {
        TekstiKappaleViite root = this;
        while (root.getVanhempi() != null) {
            root = root.getVanhempi();
        }
        return root;
    }

    static public void validoi(Validointi validointi, TekstiKappaleViite viite, Set<Kieli> julkaisukielet) {
        if (viite == null || viite.getLapset() == null) {
            return;
        }

        LokalisoituTeksti teksti = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            if (lapsi.pakollinen) {
                if (lapsi.getTekstiKappale() != null) {
                    LokalisoituTeksti.validoi(validointi, julkaisukielet, lapsi.getTekstiKappale().getNimi(), teksti);
                }
                else {
                    validointi.lisaaVirhe(Validointi.luoVirhe("tekstikappaleella-ei-lainkaan-sisaltoa", teksti));
                }
            }
            validoi(validointi, lapsi, julkaisukielet);
        }
    }
}
