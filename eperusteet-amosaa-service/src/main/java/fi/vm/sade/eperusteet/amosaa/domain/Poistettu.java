package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "poistetut")
public class Poistettu implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @NotNull
    private LokalisoituTeksti nimi;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    private Opetussuunnitelma opetussuunnitelma;

    @Getter
    @Setter
    @Column(name = "poistettu_id")
    @NotNull
    private Long poistettu;

    @Getter
    @Setter
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private SisaltoTyyppi tyyppi;

    @Getter
    @Setter
    @NotNull
    private Date pvm;

    @Getter
    @Setter
    @NotNull
    private Integer rev;

    @Getter
    @Setter
    private String muokkaajaOid;

    // TODO opetussuunnitelma_id bigint REFERENCES opetussuunnitelma,
}
