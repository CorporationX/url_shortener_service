package faang.school.urlshortenerservice;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class UrlShortenerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortenerApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
