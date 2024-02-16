package fi.vm.sade.eperusteet.amosaa.repository.liite;

import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;

import java.io.InputStream;

public interface LiiteRepositoryCustom {
    Liite add(String tyyppi, String nimi, long length, InputStream is);
}
