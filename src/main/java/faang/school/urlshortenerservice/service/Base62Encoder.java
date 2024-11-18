package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;
import io.seruco.encoding.base62.Base62;

import java.util.List;

@Component
public class Base62Encoder {
    private final Base62 base62 = Base62.createInstance();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        return new String(base62.encode(number.toString().getBytes()));
    }
}