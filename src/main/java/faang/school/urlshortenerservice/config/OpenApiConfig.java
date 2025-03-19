package faang.school.urlshortenerservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "URL Shortener Service API",
                description = "API для сервиса сокращения URL. Разработано @therealadik",
                version = "1.0",
                contact = @Contact(
                        name = "Владик (therealadik)",
                        url = "https://github.com/therealadik",
                        email = "vladerm2000@yandex.ru"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                )
        }
)
public class OpenApiConfig {
} 