package kr.co.fitzcode.common.config;

import kr.co.fitzcode.admin.mapper.SearchRankingMapper;
import kr.co.fitzcode.common.dto.SearchRankingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class SearchRankingBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final SearchRankingMapper searchRankingMapper;
    private final SqlSessionFactory sqlSessionFactory;

    // 검색 순위 배치 작업 설정
    @Bean
    public Job searchRankingJob() {
        return new JobBuilder("searchRankingJob", jobRepository)
                .start(searchRankingStep())
                .build();
    }

    // 검색 순위 처리 단계
    @Bean
    public Step searchRankingStep() {
        return new StepBuilder("searchRankingStep", jobRepository)
                .<SearchRankingDTO, SearchRankingDTO>chunk(10, transactionManager)
                .reader(searchRankingReader())
                .processor(searchRankingProcessor())
                .writer(searchRankingWriter())
                .build();
    }

    // 상위 검색 키워드 조회할때 필요한 Reader 설정
    @Bean
    public MyBatisCursorItemReader<SearchRankingDTO> searchRankingReader() {
        MyBatisCursorItemReader<SearchRankingDTO> reader = new MyBatisCursorItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("kr.co.fitzcode.admin.mapper.SearchRankingMapper.selectTopSearchKeywords");
        reader.setParameterValues(new HashMap<>());
        return reader;
    }

    // 검색 데이터 처리
    @Bean
    public ItemProcessor<SearchRankingDTO, SearchRankingDTO> searchRankingProcessor() {
        return item -> {
            item.setDate(LocalDate.now());
            return item;
        };
    }

    // 검색 순위 데이터 인서트 or 업데이트
    @Bean
    public ItemWriter<SearchRankingDTO> searchRankingWriter() {
        return items -> {
            int ranking = 1;
            for (SearchRankingDTO item : items) {
                searchRankingMapper.insertOrUpdateSearchRanking(item.getKeyword(), ranking, item.getDate());
                ranking++;
            }
        };
    }
}