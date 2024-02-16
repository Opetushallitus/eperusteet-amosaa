package fi.vm.sade.eperusteet.amosaa.repository.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kommentti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KommenttiRepository extends JpaRepository<Kommentti, Long> {
    List<Kommentti> findByTekstikappaleviiteIdAndPoistettuFalseOrderByLuotuDesc(Long tekstikappaleviiteId);

    List<Kommentti> findByTekstikappaleviiteIdOrderByLuotuDesc(Long tekstikappaleviiteId);

    List<Kommentti> findByParentId(Long parentId);
}
