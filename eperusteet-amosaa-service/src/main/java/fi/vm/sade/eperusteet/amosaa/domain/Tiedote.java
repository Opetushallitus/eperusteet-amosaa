package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by richard.vancamp on 29/03/16.
 */
@Entity
@Table(name = "tiedote")
@Getter
@Setter
@Audited
public class Tiedote implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Koulutustoimija koulutustoimija;

    private String otsikko;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    private String teksti;

    private Boolean julkinen;

    private Boolean tarkea;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luottu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date muokattu;

    private String luoja;

    private String muokkaaja;

}