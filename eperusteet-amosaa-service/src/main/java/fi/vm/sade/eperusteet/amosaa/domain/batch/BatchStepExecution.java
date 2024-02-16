package fi.vm.sade.eperusteet.amosaa.domain.batch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "batch_step_execution")
@Entity
@Getter
@Setter
public class BatchStepExecution implements Serializable {

    @Id
    @Column(name = "step_execution_id")
    private Long stepExecutionId;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "start_time")
    @JsonIgnore
    private Date startTime;

    @Column(name = "end_time")
    @JsonIgnore
    private Date endTime;

    @Column(name = "status")
    private String status;

    @Column(name = "read_count")
    private Long readCount;

    public String getJobStartTime() {
        return startTime != null ? new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(startTime) : null;
    }

    public String getJobEndTime() {
        return endTime != null ? new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(endTime) : null;
    }
}
