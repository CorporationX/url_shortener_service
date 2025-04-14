package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.dto.ThreadPoolPropsDto;
import faang.school.urlshortenerservice.mapper.ExecutorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {

    private final ThreadPoolPropsDto propsDto;
    private final ExecutorMapper executorMapper;

    @Bean(name = "hashExecutor")
    public Executor hashExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executorMapper.updateExecutor(propsDto, executor);
        executor.initialize();
        return executor;
    }
}