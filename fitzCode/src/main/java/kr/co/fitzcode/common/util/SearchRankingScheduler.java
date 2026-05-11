package kr.co.fitzcode.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchRankingScheduler {

    private final JobLauncher jobLauncher;
    private final Job searchRankingJob;

    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    public void runSearchRankingJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(searchRankingJob, jobParameters);
    }
}