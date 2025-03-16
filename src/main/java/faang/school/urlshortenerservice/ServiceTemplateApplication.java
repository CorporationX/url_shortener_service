package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.async.AsyncProperties;
import faang.school.urlshortenerservice.config.redis.RedisProperties;
import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
@EnableConfigurationProperties({AsyncProperties.class, RedisProperties.class, ShortenerProperties.class})
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
