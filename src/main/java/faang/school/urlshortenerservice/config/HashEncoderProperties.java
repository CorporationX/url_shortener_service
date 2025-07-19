package faang.school.urlshortenerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("encoding")
public class HashEncoderProperties {
    private String alphabet;
    private int hashLength;
}
