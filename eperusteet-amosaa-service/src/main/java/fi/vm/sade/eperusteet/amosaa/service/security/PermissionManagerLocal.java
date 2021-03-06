package fi.vm.sade.eperusteet.amosaa.service.security;

import java.io.Serializable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Profile("developmentPermissionOverride")
@Service
public class PermissionManagerLocal extends AbstractPermissionManager {

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetObject, TargetType target, Permission perm) {
        return true;
    }
}
