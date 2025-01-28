package faang.school.url_shortener_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("x-user-id"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("x-user-id", new SecurityScheme()
                                .name("x-user-id")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}