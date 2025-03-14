package fi.vm.sade.eperusteet.amosaa.domain.liite.version;

import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.revision.RevisionInfo;
import fi.vm.sade.eperusteet.amosaa.domain.revision.RevisionInfo_;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

class JpaWithVersioningRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        JpaWithVersioningRepository<T, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ID> entityInformation;

    public JpaWithVersioningRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    @Override
    public Revision getLatestRevision(ID id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Object[] rev = (Object[]) auditReader.createQuery()
                .forRevisionsOfEntity(entityInformation.getJavaType(), false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().desc())
                .addProjection(AuditEntity.revisionNumber())
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.muokkaajaOid.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.kommentti.getName()))
                .addOrder(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()).desc())
                .getResultList()
                .iterator()
                .next();

        return rev != null
                ? new Revision((Integer) rev[0], (Long) rev[1], (String) rev[2], (String) rev[3])
                : null;
    }

    @Override
    public List<Revision> getRevisions(ID id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) auditReader.createQuery()
                .forRevisionsOfEntity(entityInformation.getJavaType(), false, true)
                .addProjection(AuditEntity.revisionNumber())
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.muokkaajaOid.getName()))
                .addProjection(AuditEntity.revisionProperty(RevisionInfo_.kommentti.getName()))
                .addOrder(AuditEntity.revisionProperty(RevisionInfo_.timestamp.getName()).desc())
                .add(AuditEntity.id().eq(id))
                .getResultList();

        List<Revision> revisions = new ArrayList<>();
        for (Object[] result : results) {
            revisions.add(new Revision((Integer) result[0], (Long) result[1], (String) result[2], (String) result[3]));
        }

        return revisions;
    }

    @Override
    public T findRevision(ID id, Integer revisionId) {
        return AuditReaderFactory.get(entityManager).find(entityInformation.getJavaType(), id, revisionId);
    }

    @Override
    public Integer getLatestRevisionId(ID id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        final List<Number> revisions = auditReader.getRevisions(entityInformation.getJavaType(), id);
        if (revisions == null || revisions.isEmpty()) {
            return null;
        }
        return revisions.get(revisions.size() - 1).intValue();
    }

    @Override
    public T lock(T entity) {
        entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
        return entity;
    }

    @Override
    public void setRevisioKommentti(String kommentti) {
        RevisionInfo currentRevision = AuditReaderFactory.get(entityManager).getCurrentRevision(RevisionInfo.class, false);
        currentRevision.addKommentti(kommentti);
    }

}
