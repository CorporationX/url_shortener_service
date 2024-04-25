package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRedisRepositories
@EnableScheduling
public class UrlShortenerApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
