package faang.school.urlshortenerservice.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "faang.school.urlshortenerservice.repository.url.jpa"
)
public class JpaConfig {
}
