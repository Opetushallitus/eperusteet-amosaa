package fi.vm.sade.eperusteet.amosaa.repository.batch;

import fi.vm.sade.eperusteet.amosaa.domain.batch.BatchStepExecution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchStepExecutionRepository extends JpaRepository<BatchStepExecution, Long> {

    List<BatchStepExecution> findAllByOrderByStepExecutionIdDesc();
}
