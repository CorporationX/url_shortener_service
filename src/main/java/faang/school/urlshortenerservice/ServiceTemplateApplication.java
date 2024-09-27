package faang.school.urlshortenerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableFeignClients("faang.school.urlshortenerservice.client")
public class ServiceTemplateApplication {

    @Value("${threads}")
    int numberOfThreads;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    public ExecutorService urlPool() {
        return Executors.newFixedThreadPool(numberOfThreads);
    }

    @Bean
    public ExecutorService cachePool() {
        return Executors.newFixedThreadPool(numberOfThreads);
    }

}
