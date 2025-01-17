package faang.school.urlshortenerservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ulrShortenerServiceOpenApi() {
        return new OpenAPI()
                .info(new Info().title("URL-Shortener service API")
                        .description("Welcome to API documentation for URL-Shortener service")
                        .version("v1.0"));
    }
}
