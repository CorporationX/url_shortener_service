package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.context.ThreadPoolConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties(ThreadPoolConfig.class)
@EnableFeignClients("faang.school.urlshortenerservice.client")
public class ServiceUrlShortenerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceUrlShortenerApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }


}
