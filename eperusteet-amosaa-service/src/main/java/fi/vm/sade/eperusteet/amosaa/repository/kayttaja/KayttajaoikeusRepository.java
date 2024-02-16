package fi.vm.sade.eperusteet.amosaa.repository.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KayttajaoikeusRepository extends JpaRepository<Kayttajaoikeus, Long> {
    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    List<KayttajaoikeusTyyppi> findKoulutustoimijaOikeus(Long kayttaja, Long koulutustoimija);

    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    KayttajaoikeusTyyppi findKayttajaoikeus(Long kayttaja, Long koulutustoimija);

    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.oid = ?1 AND o.opetussuunnitelma.id = ?2")
    KayttajaoikeusTyyppi findKayttajaoikeus(String kayttajaOid, Long opsId);

    List<Kayttajaoikeus> findAllByKoulutustoimijaAndOpetussuunnitelma(Koulutustoimija koulutustoimija, Opetussuunnitelma ops);

    @Query("SELECT o FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    List<Kayttajaoikeus> findAllKayttajaoikeus(Long kayttaja, Long koulutustoimija);

    Kayttajaoikeus findOneByKayttajaAndKoulutustoimijaAndOpetussuunnitelma(Kayttaja a, Koulutustoimija b, Opetussuunnitelma c);

    List<Kayttajaoikeus> findAllByKayttajaAndKoulutustoimija(Kayttaja kayttaja, Koulutustoimija koulutustoimija);

    List<Kayttajaoikeus> findAllByKayttaja(Kayttaja kayttaja);

    Kayttajaoikeus findOneByKayttajaAndOpetussuunnitelma(Kayttaja kayttaja, Opetussuunnitelma ops);
}
