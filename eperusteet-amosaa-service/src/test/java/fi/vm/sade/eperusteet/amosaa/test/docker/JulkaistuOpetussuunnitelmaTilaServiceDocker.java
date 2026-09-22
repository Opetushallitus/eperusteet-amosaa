package fi.vm.sade.eperusteet.amosaa.test.docker;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaistuOpetussuunnitelmaTilaRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaistuOpetussuunnitelmaTilaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Profile("docker")
public class JulkaistuOpetussuunnitelmaTilaServiceDocker implements JulkaistuOpetussuunnitelmaTilaService {

    @Autowired
    private JulkaistuOpetussuunnitelmaTilaRepository julkaistuOpetussuunnitelmaTilaRepository;

    @Override
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {
        julkaistuOpetussuunnitelmaTilaRepository.save(julkaistuOpetussuunnitelmaTila);
    }
}
