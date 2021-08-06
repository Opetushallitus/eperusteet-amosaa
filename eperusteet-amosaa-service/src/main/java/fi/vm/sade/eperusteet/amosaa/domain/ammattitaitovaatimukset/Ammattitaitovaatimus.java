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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author isaul
 */
@Entity
@Table(name = "ammattitaitovaatimus")
@Audited
public class Ammattitaitovaatimus implements Serializable, Copyable<Ammattitaitovaatimus> {

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
    private LokalisoituTeksti selite;

    @Getter
    @Setter
    @Column(name = "koodi")
    private String ammattitaitovaatimusKoodi;

    @Getter
    @Setter
    private Integer jarjestys;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private AmmattitaitovaatimuksenKohde ammattitaitovaatimuksenkohde;

    @Override
    public Ammattitaitovaatimus copy(boolean deep) {
        Ammattitaitovaatimus ammattitaitovaatimus = new Ammattitaitovaatimus();

        ammattitaitovaatimus.setSelite(this.getSelite());
        ammattitaitovaatimus.setAmmattitaitovaatimusKoodi(this.getAmmattitaitovaatimusKoodi());
        ammattitaitovaatimus.setJarjestys(this.getJarjestys());

        return ammattitaitovaatimus;
    }
}
