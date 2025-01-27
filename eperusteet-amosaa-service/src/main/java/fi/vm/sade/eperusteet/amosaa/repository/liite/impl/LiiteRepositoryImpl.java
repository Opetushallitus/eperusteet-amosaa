package fi.vm.sade.eperusteet.amosaa.repository.liite.impl;

import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.repository.liite.LiiteRepositoryCustom;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import jakarta.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;

public class LiiteRepositoryImpl implements LiiteRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public Liite add(String tyyppi, String nimi, long length, InputStream is) {
        Blob blob = null;
        try {
            blob = BlobProxy.generateProxy(IOUtils.toByteArray(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Liite liite = new Liite(tyyppi, nimi, blob);
        em.persist(liite);
        return liite;
    }

}
