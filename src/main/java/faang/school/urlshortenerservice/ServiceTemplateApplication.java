package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.properties.CleanerProperties;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.properties.ShortenerProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
@EnableConfigurationProperties({
        HashProperties.class,
        CleanerProperties.class,
        ShortenerProperties.class
})
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
