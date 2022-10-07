package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private MaintenanceService maintenanceService;

    @Scheduled(cron = "0 0 4 * * *")
    public void scheduledKoulutustoimijatCaching() {
        log.info("Starting scheduled run to cache all koulutustoimijat.");
        SecurityContextHolder.getContext().setAuthentication(useAdminAuth());

        maintenanceService.clearCache("koulutustoimijat");
        List<KoulutustoimijaBaseDto> kaikkiKoulutustoimijat = koulutustoimijaService.getKoulutustoimijat();

        SecurityContextHolder.getContext().setAuthentication(null);
        log.info("Cached {} koulutustoimijaa.", kaikkiKoulutustoimijat.size());
    }

    private Authentication useAdminAuth() {
        // Käytetään pääkäyttäjän oikeuksia.
        return new UsernamePasswordAuthenticationToken("system",
                "ROLE_ADMIN", AuthorityUtils.createAuthorityList("ROLE_ADMIN",
                "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.00000000001"));
    }
}
