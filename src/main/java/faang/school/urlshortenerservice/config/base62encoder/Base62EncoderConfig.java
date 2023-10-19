package faang.school.urlshortenerservice.config.base62encoder;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class Base62EncoderConfig {

    private final int ENCODING_FACTOR = 62;
    private final String BASE_62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
}
