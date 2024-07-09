package faang.school.urlshortenerservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    @Value("services.hash.base62")
    public String BASE62;
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, BASE62.charAt((int) (number % 62)));
            number /= BASE;
        } while (number > 0);
        return stringBuilder.toString();
    }
}
