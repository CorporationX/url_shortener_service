package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    @Value("${hashgenerator.base62}")
    private String base62;

    public List<String> encode(List<Long> values) {
        return values.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long value) {
        StringBuilder encoded = new StringBuilder();
        int length = base62.length();

        while (value > 0) {
            encoded.append(base62.charAt((int) (value % length)));
            value /= length;
        }
        return encoded.toString();
    }
}