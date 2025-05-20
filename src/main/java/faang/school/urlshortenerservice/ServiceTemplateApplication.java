package faang.school.urlshortenerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableFeignClients("faang.school.urlshortenerservice.client")
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    @SuppressWarnings("unused")
    public ExecutorService hashGeneratorExecutorService(
            @Value("${hash-generator.thread-pool-size:0}") int treadPoolSize) {
        if (treadPoolSize <= 0) {
            treadPoolSize = Runtime.getRuntime().availableProcessors() + 1;
        }

        return Executors.newFixedThreadPool(treadPoolSize);
    }
}
