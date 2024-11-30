package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients(basePackages = {"faang.school.urlshortenerservice.client"})
@ConfigurationPropertiesScan(basePackages = {"faang.school.urlshortenerservice.config"})
@SpringBootApplication
public class UrlShortenerServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
