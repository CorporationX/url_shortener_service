package faang.school.urlshortenerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urlShortenerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Deema's URL shortener API")
                        .description("Made by Deema, good luck =).")
                        .version("v1.0"));
    }
}
