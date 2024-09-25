package faang.school.urlshortenerservice.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    @Value("${base62}")
    private String BASE62_CHARS;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        for (Long uniqueNumber : numbers) {
            StringBuilder hash = new StringBuilder();
            while (uniqueNumber > 0) {
                hash.append(BASE62_CHARS.charAt((int) (uniqueNumber % 62)));
                uniqueNumber /= 62;
                hash.append(BASE62_CHARS.charAt((int) (uniqueNumber % BASE62_CHARS.length())));
                uniqueNumber /= BASE62_CHARS.length();
            }
            hashes.add(hash.toString());
        }
        return hashes;
    }
}
