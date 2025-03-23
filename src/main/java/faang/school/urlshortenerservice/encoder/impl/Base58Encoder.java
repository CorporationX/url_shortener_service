package faang.school.urlshortenerservice.encoder.impl;

import faang.school.urlshortenerservice.encoder.AbstractEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "shortener.encoder.codebase", havingValue = "base58")
public class Base58Encoder extends AbstractEncoder {

    private static final String BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public Base58Encoder(@Value("${shortener.encoder.mix-parameter:3}") Integer mixParameter) {
        super(BASE58_CHARS, mixParameter);
    }
}
