package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpetussuunnitelmaMuokkaustieto;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpetussuunnitelmaMuokkaustietoRepository extends JpaRepository<OpetussuunnitelmaMuokkaustieto, Long> {

    List<OpetussuunnitelmaMuokkaustieto> findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, Pageable pageable);

    default List<OpetussuunnitelmaMuokkaustieto> findTop10ByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(Long opsId, Date viimeisinLuontiaika, int lukumaara) {
        return findByOpetussuunnitelmaIdAndLuotuBeforeOrderByLuotuDesc(opsId, viimeisinLuontiaika, PageRequest.of(0, Math.min(lukumaara, 100)));
    }

    List<OpetussuunnitelmaMuokkaustieto> findByKohdeId(Long kohdeId);
}
