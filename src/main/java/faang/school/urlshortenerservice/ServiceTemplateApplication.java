package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.GeneratorPoolProperties;
import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.config.RedisProperties;
import faang.school.urlshortenerservice.config.SchedulerPoolProperties;
import faang.school.urlshortenerservice.config.UrlServiceProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableFeignClients("faang.school.urlshortenerservice.client")
@EnableConfigurationProperties({GeneratorPoolProperties.class, HashGeneratorProperties.class, LocalCacheProperties.class,
RedisProperties.class, SchedulerPoolProperties.class, UrlServiceProperties.class})
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
