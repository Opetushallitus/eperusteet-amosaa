package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tiedote")
@Getter
@Setter
public class Tiedote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Koulutustoimija koulutustoimija;

    private String otsikko;

    private String teksti;

    private Boolean julkinen;

    private Boolean tarkea;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date muokattu;

    private String luoja;

    private String muokkaaja;

}
