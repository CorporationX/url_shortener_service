package faang.school.urlshortenerservice.encoder.impl;

import faang.school.urlshortenerservice.encoder.AbstractEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "shortener.encoder", havingValue = "base62")
public class Base62Encoder extends AbstractEncoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public Base62Encoder() {
        super(BASE62_CHARS);
    }

    @Override
    public String encode(Long sequenceNumber) {
        return commonEncode(sequenceNumber);
    }

    @Override
    public List<String> encode(List<Long> sequenceNumbers) {
        return commonEncode(sequenceNumbers);
    }
}
