package fi.vm.sade.eperusteet.amosaa.repository.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import java.util.Collection;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KayttajaRepository extends CustomJpaRepository<Kayttaja, Long> {
    Kayttaja findOneByOid(String oid);

    Collection<Kayttaja> findByOidIn(Collection<String> oids);
}
