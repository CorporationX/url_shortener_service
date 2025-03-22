package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    @Value("${hash-generator.characters}")
    private String characters;

    public List<Hash> encodeBatch(List<Long> numbers) {
        return numbers.stream()
            .filter(Objects::nonNull)
            .map(this::encode)
            .toList();
    }

    private Hash encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int index = (int) (number % characters.length());
            sb.append(characters.charAt(index));
            number /= characters.length();
        }
        return new Hash(sb.toString());
    }
}
