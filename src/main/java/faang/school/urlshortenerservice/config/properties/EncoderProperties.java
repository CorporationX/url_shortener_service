package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shortener.encoder")
public record EncoderProperties(
        String alphabet,
        int length,
        long multiplier
) {
    public int base() {
        return alphabet.length();
    }

    public long space() { return (long) Math.pow(base(), length); }
}
