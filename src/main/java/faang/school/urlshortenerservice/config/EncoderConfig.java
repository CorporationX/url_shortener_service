package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.util.DecimalToBaseConverter;
import faang.school.urlshortenerservice.util.BaseEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncoderConfig {
    @Value("${encoder.settings.base}")
    private int base;

    @Value("${encoder.settings.chars}")
    private String chars;

    @Bean
    public BaseEncoder configuredEncoder() {
        return new DecimalToBaseConverter(base, chars);
    }
}