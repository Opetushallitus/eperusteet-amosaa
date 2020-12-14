package fi.vm.sade.eperusteet.amosaa.service.util;

import org.springframework.security.access.prepost.PreAuthorize;

public interface MaintenanceService {

    @PreAuthorize("isAuthenticated()")
    void updateKoulutustoimijaTyypit();
}
