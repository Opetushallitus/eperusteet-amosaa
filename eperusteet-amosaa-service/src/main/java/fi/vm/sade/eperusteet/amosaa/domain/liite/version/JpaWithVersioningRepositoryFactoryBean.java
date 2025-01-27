package fi.vm.sade.eperusteet.amosaa.domain.liite.version;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;
import java.io.Serializable;

public class JpaWithVersioningRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable> extends JpaRepositoryFactoryBean<R, T, ID> {

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public JpaWithVersioningRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        return new JpaWithVersioningRepositoryFactory<>(entityManager);
    }

    private static class JpaWithVersioningRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

        private final EntityManager entityManager;

        public JpaWithVersioningRepositoryFactory(EntityManager entityManager) {
            super(entityManager);

            this.entityManager = entityManager;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation metadata, EntityManager entityManager) {

            if (JpaWithVersioningRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                if (metadata.getDomainType().getAnnotation(Audited.class) == null) {
                    throw new JpaWithVersioningRepository.DomainClassNotAuditedException(metadata.getDomainType());
                }
                return new JpaWithVersioningRepositoryImpl<>(getEntityInformation((Class<T>) metadata.getDomainType()), entityManager);
            } else {
                return super.getTargetRepository(metadata, entityManager);
            }
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return JpaWithVersioningRepository.class.isAssignableFrom(metadata.getRepositoryInterface())
                    ? JpaWithVersioningRepository.class : super.getRepositoryBaseClass(metadata);
        }
    }
}
