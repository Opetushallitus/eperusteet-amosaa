package fi.vm.sade.eperusteet.amosaa.repository.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaKuittaus;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KayttajaKuittausRepository extends JpaRepository<KayttajaKuittaus, Long> {
    @Query("SELECT k.tiedote FROM KayttajaKuittaus k WHERE k.kayttaja = ?1")
    Set<Long> findAllByKayttaja(Long kayttajaId);
}
