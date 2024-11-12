package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.base.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.aspectj.SpringConfiguredConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("school.faang.servicetemplate.client")
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
