package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "opetussuunnitelma_muokkaustieto")
public class OpetussuunnitelmaMuokkaustieto implements Serializable {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti nimi;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private MuokkausTapahtuma tapahtuma;

    @Column(name = "opetussuunnitelma_id")
    private Long opetussuunnitelmaId;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NavigationType kohde;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    private String muokkaaja;

    @NotNull
    @Column(name = "kohde_id")
    private Long kohdeId;

    private String lisatieto;

    @NotNull
    private boolean poistettu = false;
}
