package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
