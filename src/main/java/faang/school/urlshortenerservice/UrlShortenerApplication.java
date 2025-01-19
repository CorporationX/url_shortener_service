package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ConfigurationPropertiesScan
public class UrlShortenerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
