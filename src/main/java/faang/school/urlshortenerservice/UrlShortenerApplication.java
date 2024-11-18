package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableFeignClients("faang.school.urlshortenerservice.client")
@ConfigurationPropertiesScan("faang.school.urlshortenerservice.config")
public class UrlShortenerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
