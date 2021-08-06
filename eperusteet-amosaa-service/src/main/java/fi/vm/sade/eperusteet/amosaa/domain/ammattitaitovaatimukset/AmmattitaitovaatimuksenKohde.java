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
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

/**
 * @author isaul
 */
@Entity
@Table(name = "ammattitaitovaatimuksenkohde")
@Audited
public class AmmattitaitovaatimuksenKohde implements Serializable, Copyable<AmmattitaitovaatimuksenKohde> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti otsikko;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti selite;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenkohdealue;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    @OrderColumn(name = "jarjestys")
    private List<Ammattitaitovaatimus> vaatimukset = new ArrayList<>();

    @Override
    public AmmattitaitovaatimuksenKohde copy(boolean deep) {
        AmmattitaitovaatimuksenKohde kohde = new AmmattitaitovaatimuksenKohde();

        kohde.setOtsikko(this.getOtsikko());
        kohde.setSelite(this.getSelite());

        if (!ObjectUtils.isEmpty(this.getVaatimukset())) {
            kohde.setVaatimukset(this.getVaatimukset().stream()
                    .map(Ammattitaitovaatimus::copy)
                    .collect(Collectors.toList()));
            kohde.getVaatimukset().forEach(kj -> kj.setAmmattitaitovaatimuksenkohde(this));
        }

        return kohde;
    }
}
