package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class UrlShortenerApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
