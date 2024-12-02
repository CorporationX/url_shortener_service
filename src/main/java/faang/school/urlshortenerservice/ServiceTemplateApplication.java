package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.redis.RedisCacheProperties;
import faang.school.urlshortenerservice.config.threads.ThreadPoolProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableFeignClients("faang.school.urlshortenerservice.client")
@EnableConfigurationProperties({ThreadPoolProperties.class, RedisCacheProperties.class})
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
