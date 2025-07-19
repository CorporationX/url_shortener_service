package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.HashEncoderProperties;
import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.List;

@Component
public class Base62Encoder {
    private final Sqids sqids;

    public Base62Encoder(HashEncoderProperties properties) {
        this.sqids = Sqids.builder()
                .alphabet(properties.getAlphabet())
                .minLength(properties.getHashLength())
                .build();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(List::of)
                .map(sqids::encode)
                .toList();
    }
}