package fi.vm.sade.eperusteet.amosaa.config;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Profile("!test")
@Configuration
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(getSimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    private ThreadPoolTaskExecutor getSimpleAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(5);
        return executor;
    }

    @Bean
    protected Step julkaisuJobStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Long> julkaisuJobReader,
            ItemProcessor<Long, Julkaisu> julkaisuJobProcessor,
            ItemWriter<Julkaisu> julkaisuJobWriter) {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setPropagationBehavior(Propagation.NEVER.value());

        return new StepBuilder("julkaisuJobStep", jobRepository)
                .<Long, Julkaisu>chunk(10, transactionManager)
                .reader(julkaisuJobReader)
                .processor(julkaisuJobProcessor)
                .writer(julkaisuJobWriter)
                .transactionAttribute(attribute)
                .build();
    }

    @Bean
    public Job julkaisuJob(JobRepository jobRepository, Step julkaisuJobStep) {
        return new JobBuilder("julkaisuJob", jobRepository)
                .start(julkaisuJobStep)
                .build();
    }

    @Bean
    protected Step perusteKoulutuksetMigrateJobStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Long> koulutuskoodiConvertReader,
            ItemProcessor<Long, CachedPeruste> koulutuskoodiConvertProcessor,
            ItemWriter<CachedPeruste> koulutuskoodiConvertWriter) {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setPropagationBehavior(Propagation.NEVER.value());

        return new StepBuilder("perusteKoulutuksetMigrateJobStep", jobRepository)
                .<Long, CachedPeruste>chunk(10, transactionManager)
                .reader(koulutuskoodiConvertReader)
                .processor(koulutuskoodiConvertProcessor)
                .writer(koulutuskoodiConvertWriter)
                .transactionAttribute(attribute)
                .build();
    }

    @Bean
    public Job perusteKoulutuksetMigrateJob(JobRepository jobRepository, Step perusteKoulutuksetMigrateJobStep) {
        return new JobBuilder("perusteKoulutuksetMigrateJob", jobRepository)
                .start(perusteKoulutuksetMigrateJobStep)
                .build();
    }
}
