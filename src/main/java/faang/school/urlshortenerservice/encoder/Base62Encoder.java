package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE_62 = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    public List<Hash> encode(List<Long> range) {
        return range.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62.charAt((int) (number % BASE_62.length())));
            number /= BASE_62.length();
        }
        return builder.toString();
    }
}
