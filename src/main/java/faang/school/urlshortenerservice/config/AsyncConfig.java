package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
	@Value("${hash.generator.executor.core-pool-size:2}")
	private int corePoolSize;
	@Value("${hash.generator.executor.max-pool-size:2}")
	private int maxPoolSize;
	@Value("${hash.generator.executor.queue-capacity:50}")
	private int queueCapacity;

	@Value("${hash.cache.executor.core-pool-size:2}")
	private int cacheCorePoolSize;
	@Value("${hash.cache.executor.max-pool-size:2}")
	private int cacheMaxPoolSize;
	@Value("${hash.cache.executor.queue-capacity:100}")
	private int cacheQueueCapacity;

	@Bean("hashGeneratorExecutor")
	public Executor hashGeneratorExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("hash-generator-");
		executor.initialize();
		return executor;
	}

	@Bean("hashCacheExecutor")
	public Executor hashCacheExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(cacheCorePoolSize);
		executor.setMaxPoolSize(cacheMaxPoolSize);
		executor.setQueueCapacity(cacheQueueCapacity);
		executor.setThreadNamePrefix("hash-cache-");
		executor.initialize();
		return executor;
	}
}
