package faang.school.urlshortenerservice.encoder.impl;

import faang.school.urlshortenerservice.encoder.AbstractEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "shortener.encoder", havingValue = "base58")
public class Base58Encoder extends AbstractEncoder {
    private static final String BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public Base58Encoder() {
        super(BASE58_CHARS);
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
