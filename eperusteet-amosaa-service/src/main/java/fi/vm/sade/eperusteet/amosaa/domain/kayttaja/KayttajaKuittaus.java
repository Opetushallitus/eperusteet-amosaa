package fi.vm.sade.eperusteet.amosaa.domain.kayttaja;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kayttaja_kuittaus")
public class KayttajaKuittaus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private Long kayttaja;

    @Getter
    @Setter
    private Long tiedote;

    public KayttajaKuittaus() {
    }

    public KayttajaKuittaus(Long kayttaja, Long tiedote) {
        this.kayttaja = kayttaja;
        this.tiedote = tiedote;
    }
}
