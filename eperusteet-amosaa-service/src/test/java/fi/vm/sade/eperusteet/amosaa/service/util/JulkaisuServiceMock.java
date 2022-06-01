package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class JulkaisuServiceMock implements JulkaisuService {
    @Override
    public List<JulkaisuBaseDto> getJulkaisut(long ktId, long opsId) {
        return null;
    }

    @Override
    public JulkaisuBaseDto teeJulkaisu(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        return null;
    }

    @Override
    public JulkaisuBaseDto aktivoiJulkaisu(long ktId, long opsId, int revision) {
        return null;
    }

    @Override
    public void kooditaSisaltoviitteet(long ktId, long opsId) {
        
    }
}
