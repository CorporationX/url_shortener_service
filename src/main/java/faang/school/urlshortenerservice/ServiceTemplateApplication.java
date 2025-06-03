package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.config.properties.HashThreadPoolProps;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({HashProperties.class, HashThreadPoolProps.class, CacheProperties.class})
@EnableScheduling
@EnableAsync
@EnableFeignClients("faang.school.urlshortenerservice.client")
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
