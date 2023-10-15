package faang.school.urlshortenerservice;

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients("faang.school.urlshortenerservice.client")
@OpenAPIDefinition(
        info = @Info(
                title = "Url shortener service API",
                version = "1.0.0",
                description = "Service for creating short links",
                contact = @Contact(name = "FAANG school", url = "https://faang.school")
        )
)
public class ServiceTemplateApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceTemplateApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
