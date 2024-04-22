package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpetussuunnitelmaCustomRepository {
    Page<Opetussuunnitelma> findBy(PageRequest page, OpetussuunnitelmaQueryDto queryDto);
    Page<Opetussuunnitelma> findBy(PageRequest page, OpsHakuDto queryDto);
}
