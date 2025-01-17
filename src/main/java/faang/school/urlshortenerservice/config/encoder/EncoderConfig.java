package faang.school.urlshortenerservice.config.encoder;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncoderConfig {

    @Value("${base-encoder.base}")
    private Integer base;

    @Value("${base-encoder.characters}")
    private String characters;

    @Bean
    public Integer base() {
        return base;
    }

    @Bean
    public String characters() {
        return characters;
    }

    @Bean
    public Base62Encoder base62Encoder(Integer base, String characters) {
        return new Base62Encoder(base, characters);
    }
}
