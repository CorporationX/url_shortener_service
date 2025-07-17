package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.HashEncoderProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final HashEncoderProperties properties;
    private final int base;

    public Base62Encoder(HashEncoderProperties properties) {
        this.properties = properties;
        this.base = properties.getAlphabet().length();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::shuffleNumber)
                .map(this::encodeToBase62)
                .toList();
    }

    private long shuffleNumber(long number) {
        return (number * properties.getShuffleKey()) % (properties.getMaxNumber() + 1);
    }

    private String encodeToBase62(long shuffledNumber) {
        StringBuilder sb = new StringBuilder();
        long argumentCopy = shuffledNumber;

        for (int i = 0; i < properties.getHashLength(); i++) {
            sb.insert(0, properties.getAlphabet().charAt((int) (argumentCopy % base)));
            argumentCopy /= base;
        }

        return sb.toString();
    }
}
