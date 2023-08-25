package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "docker"})
public class DokumenttiBuilderServiceMock implements DokumenttiBuilderService  {
    @Override
    public byte[] generatePdf(Opetussuunnitelma ops, Dokumentti dokumentti, Kieli kieli) {
        return new byte[0];
    }
}
