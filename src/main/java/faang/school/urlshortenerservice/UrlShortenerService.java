package faang.school.urlshortenerservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
public class UrlShortenerService {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerService.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}