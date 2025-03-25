package faang.school.urlshortenerservice.encoder.impl;

import faang.school.urlshortenerservice.encoder.AbstractEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "shortener.encoder.codebase", havingValue = "base62")
public class Base62Encoder extends AbstractEncoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public Base62Encoder(@Value("${shortener.encoder.mix-parameter:3}") Integer mixParameter) {
        super(BASE62_CHARS, mixParameter);
    }

}
