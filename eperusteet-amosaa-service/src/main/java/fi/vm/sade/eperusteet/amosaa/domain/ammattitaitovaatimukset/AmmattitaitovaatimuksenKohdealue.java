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
package fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author autio
 */
@Entity
@Table(name = "ammattitaitovaatimuksenkohdealue")
@Audited
public class AmmattitaitovaatimuksenKohdealue implements Serializable {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti otsikko;

    @OneToMany(mappedBy = "ammattitaitovaatimuksenkohdealue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<AmmattitaitovaatimuksenKohde> vaatimuksenKohteet = new ArrayList<>();

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ammattitaitovaatimuksenkohdealue_omatutkinnonosa",
            inverseJoinColumns = @JoinColumn(name = "omatutkinnonosa_id"),
            joinColumns = @JoinColumn(name = "ammattitaitovaatimuksenkohdealue_id"))
    private Set<OmaTutkinnonosa> tutkinnonOsat = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmmattitaitovaatimuksenKohdealue that = (AmmattitaitovaatimuksenKohdealue) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public void connectAmmattitaitovaatimuksetToKohdealue(
            AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue) {
        for (AmmattitaitovaatimuksenKohde ammattitaitovaatimuksenKohde : this.getVaatimuksenKohteet()) {
            ammattitaitovaatimuksenKohde.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);
            for (Ammattitaitovaatimus ammattitaitovaatimus : ammattitaitovaatimuksenKohde.getVaatimukset()) {
                ammattitaitovaatimus.setAmmattitaitovaatimuksenkohde(ammattitaitovaatimuksenKohde);
            }
        }
    }
}
