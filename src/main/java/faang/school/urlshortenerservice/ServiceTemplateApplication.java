package faang.school.urlshortenerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        return new RestTemplate(requestFactory);
    }
}
