package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
@ConfigurationPropertiesScan("faang.school.urlshortenerservice.config")
@EnableConfigurationProperties
@SpringBootApplication
public class AppShortLink {
    public static void main(String[] args) {

        new SpringApplicationBuilder(AppShortLink.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
