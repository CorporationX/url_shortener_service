package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Конфигурационный класс для настройки пула потоков.
 * Создает фиксированный пул потоков, размер которого задается в конфигурации.
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * Количество потоков в пуле. Задается через конфигурацию (application.properties).
     */
    @Value("${spring.threads.pool.number}")
    private int threads;

    /**
     * Создает и возвращает ExecutorService с фиксированным пулом потоков.
     *
     * @return ExecutorService с фиксированным количеством потоков.
     */
    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(threads);
    }
}
