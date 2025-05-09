package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableRetry
public class UrlShortenerServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
