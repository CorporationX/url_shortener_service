package faang.school.urlshortenerservice.service.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class Base62Encoder {
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62.charAt((int) number % BASE62.length()));
            number /= BASE62.length();
        }
        return builder.toString();
    }
}
