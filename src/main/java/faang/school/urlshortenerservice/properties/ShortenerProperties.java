package faang.school.urlshortenerservice.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Getter
@Setter
@ConfigurationProperties(prefix = "shortener")
public class ShortenerProperties {

    @NotNull
    private URI baseUrl;
}
