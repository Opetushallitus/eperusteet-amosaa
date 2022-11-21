package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaistuOpetussuunnitelmaTila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JulkaistuOpetussuunnitelmaTilaRepository extends JpaRepository<JulkaistuOpetussuunnitelmaTila, Long> {
}