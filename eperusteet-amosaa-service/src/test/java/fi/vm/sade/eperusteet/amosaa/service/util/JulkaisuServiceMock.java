package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuTila;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("test")
public class JulkaisuServiceMock implements JulkaisuService {
    @Override
    public List<JulkaisuBaseDto> getJulkaisut(long ktId, long opsId) {
        return null;
    }

    @Override
    public CompletableFuture<Void> teeJulkaisu(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        return null;
    }

    @Override
    public CompletableFuture<Void> teeJulkaisuAsync(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
        return null;
    }

    @Override
    public JulkaisuBaseDto aktivoiJulkaisu(long ktId, long opsId, int revision) {
        return null;
    }

    @Override
    public void kooditaSisaltoviitteet(long ktId, long opsId) {

    }

    @Override
    public boolean onkoMuutoksia(long ktId, long opsId) {
        return false;
    }

    @Override
    public JulkaisuTila viimeisinJulkaisuTila(long ktId, long opsId) {
        return null;
    }

    @Override
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {

    }

}
