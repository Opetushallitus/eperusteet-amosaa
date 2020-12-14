package fi.vm.sade.eperusteet.amosaa.service.util.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import java.util.HashSet;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Override
    public void updateKoulutustoimijaTyypit() {
        koulutustoimijaRepository.findAll().forEach(koulutustoimija -> {
            JsonNode organisaatio = organisaatioService.getOrganisaatio(koulutustoimija.getOrganisaatio());
            if (organisaatio != null) {
                koulutustoimija.setOrganisaatioTyypit(new HashSet<>());
                if (organisaatio.get("tyypit") != null && organisaatio.get("tyypit").isArray()) {
                    for (final JsonNode objNode : organisaatio.get("tyypit")) {
                        koulutustoimija.addOrganisaatioTyyppi(objNode.asText());
                    }
                }
                koulutustoimijaRepository.save(koulutustoimija);
            }
        });
    }
}
