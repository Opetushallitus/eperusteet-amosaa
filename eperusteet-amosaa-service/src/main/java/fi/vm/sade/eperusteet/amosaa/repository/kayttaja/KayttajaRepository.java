package fi.vm.sade.eperusteet.amosaa.repository.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KayttajaRepository extends JpaRepository<Kayttaja, Long> {
    Kayttaja findOneByOid(String oid);

    Collection<Kayttaja> findByOidIn(Collection<String> oids);
}
