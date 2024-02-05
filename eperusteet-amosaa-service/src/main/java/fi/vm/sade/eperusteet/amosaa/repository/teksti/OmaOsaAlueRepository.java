package fi.vm.sade.eperusteet.amosaa.repository.teksti;


import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmaOsaAlueRepository extends JpaWithVersioningRepository<OmaOsaAlue, Long> {

    @Query(value = "SELECT DISTINCT osaalueet " +
            "FROM SisaltoViite sv " +
            "JOIN sv.osaAlueet osaalueet " +
            "JOIN osaalueet.toteutukset tot " +
            "WHERE sv.owner.id = :opetussuunnitelmaId AND tot.oletustoteutus = true")
    List<OmaOsaAlue> findTutkinnonosienOletusotetutukset(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId);
}
