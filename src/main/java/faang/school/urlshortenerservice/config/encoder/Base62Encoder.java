package faang.school.urlshortenerservice.config.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    @Value("${alphabet}")
    private String alphabetBase62;

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>();
        numbers.forEach(n -> hashes.add(applyBase62Encoder(n)));
        return hashes;
    }

    private Hash applyBase62Encoder(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(alphabetBase62.charAt((int) (number % alphabetBase62.length())));
            number /= alphabetBase62.length();
        }
        return new Hash(builder.toString());
    }
}
